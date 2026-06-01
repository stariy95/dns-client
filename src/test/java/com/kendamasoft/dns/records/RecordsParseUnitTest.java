package com.kendamasoft.dns.records;

import com.kendamasoft.dns.protocol.Buffer;
import com.kendamasoft.dns.protocol.RecordType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests RDATA parsing of each public record type by calling the public
 * {@link AbstractRecord#parseData(short, Buffer)} contract with crafted bytes and asserting
 * public getters / {@code toString()}. These are characterization tests: they pin current
 * behavior of the parsing contract.
 */
public class RecordsParseUnitTest {

    /** Encode a dotted name as DNS labels: "www.example.com" -> 03 www 07 example 03 com 00. */
    private static byte[] encodeName(String dotted) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (String label : dotted.split("\\.")) {
            out.write(label.length());
            for (char c : label.toCharArray()) {
                out.write(c);
            }
        }
        out.write(0);
        return out.toByteArray();
    }

    private static byte[] concat(byte[]... parts) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (byte[] part : parts) {
            out.write(part, 0, part.length);
        }
        return out.toByteArray();
    }

    private static byte[] ascii(String s) {
        return s.getBytes(StandardCharsets.US_ASCII);
    }

    /** Encode a {@code <character-string>}: 1-byte length prefix + the ASCII bytes. */
    private static byte[] charString(String s) {
        return concat(new byte[]{(byte) s.length()}, ascii(s));
    }

    /** Big-endian unsigned 16-bit. */
    private static byte[] u16(int v) {
        return new byte[]{(byte) (v >>> 8), (byte) v};
    }

    /** Big-endian unsigned 32-bit. */
    private static byte[] u32(long v) {
        return new byte[]{(byte) (v >>> 24), (byte) (v >>> 16), (byte) (v >>> 8), (byte) v};
    }

    @Nested
    class ARecordTests {
        @Test
        public void parsesIpv4Address() {
            byte[] rdata = {93, (byte) 184, (byte) 216, 34};
            ARecord record = new ARecord();
            record.parseData((short) rdata.length, new Buffer(rdata));

            assertArrayEquals(rdata, record.getAddress());
            assertEquals("A 93.184.216.34", record.toString());
        }
    }

    @Nested
    class AAAARecordTests {
        @Test
        public void parsesIpv6Address() {
            byte[] rdata = {0x20, 0x01, 0x0d, (byte) 0xb8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0x01};
            AAAARecord record = new AAAARecord();
            record.parseData((short) rdata.length, new Buffer(rdata));

            assertArrayEquals(rdata, record.getAddress());
            // IPv6 textual form is JDK-formatted, so only assert the type prefix.
            assertTrue(record.toString().startsWith("AAAA "));
        }
    }

    @Nested
    class CNAMERecordTests {
        @Test
        public void parsesCanonicalName() {
            byte[] rdata = encodeName("www.example.com");
            CNAMERecord record = new CNAMERecord();
            record.parseData((short) rdata.length, new Buffer(rdata));

            assertEquals("www.example.com.", record.getName());
            assertEquals("CNAME www.example.com.", record.toString());
        }
    }

    @Nested
    class NSRecordTests {
        @Test
        public void parsesNameServer() {
            byte[] rdata = encodeName("ns1.example.com");
            NSRecord record = new NSRecord();
            record.parseData((short) rdata.length, new Buffer(rdata));

            assertEquals("ns1.example.com.", record.getName());
            assertEquals("NS ns1.example.com.", record.toString());
        }
    }

    @Nested
    class PTRRecordTests {
        @Test
        public void parsesPointerName() {
            byte[] rdata = encodeName("host.example.com");
            PTRRecord record = new PTRRecord();
            record.parseData((short) rdata.length, new Buffer(rdata));

            assertEquals("host.example.com.", record.getName());
            assertEquals("PTR host.example.com.", record.toString());
        }
    }

    @Nested
    class MXRecordTests {
        @Test
        public void parsesPreferenceAndExchange() {
            byte[] rdata = concat(new byte[]{0x00, 0x0A}, encodeName("mail.example.com"));
            MXRecord record = new MXRecord();
            record.parseData((short) rdata.length, new Buffer(rdata));

            assertEquals(10, record.getPriority());
            assertEquals("mail.example.com.", record.getName());
            assertEquals("MX 10 mail.example.com.", record.toString());
        }
    }

    @Nested
    class SOARecordTests {
        @Test
        public void parsesAllFields() {
            byte[] rdata = concat(
                    encodeName("ns.example.com"),
                    encodeName("admin.example.com"),
                    new byte[]{0, 0, 0, 1, 0, 0, 0, 2, 0, 0, 0, 3, 0, 0, 0, 4, 0, 0, 0, 5});
            SOARecord record = new SOARecord();
            record.parseData((short) rdata.length, new Buffer(rdata));

            // SOARecord has no public getters, so assert via the dig-style toString().
            assertEquals("SOA ns.example.com. admin.example.com. 1 2 3 4 5", record.toString());
        }
    }

    @Nested
    class TXTRecordTests {
        @Test
        public void parsesPlainText() {
            byte[] rdata = concat(new byte[]{0x05}, ascii("hello"));
            TXTRecord record = new TXTRecord();
            record.parseData((short) rdata.length, new Buffer(rdata));

            assertEquals("hello", record.getText());
            assertEquals("TXT hello", record.toString());
        }

        @Test
        public void quotesTextContainingSpaces() {
            byte[] rdata = concat(new byte[]{0x0b}, ascii("hello world"));
            TXTRecord record = new TXTRecord();
            record.parseData((short) rdata.length, new Buffer(rdata));

            assertEquals("\"hello world\"", record.getText());
            assertEquals("TXT \"hello world\"", record.toString());
        }
    }

    @Nested
    class SPFRecordTests {
        @Test
        public void parsesLikeTxtButLabeledSpf() {
            byte[] rdata = concat(new byte[]{0x06}, ascii("v=spf1"));
            SPFRecord record = new SPFRecord();
            record.parseData((short) rdata.length, new Buffer(rdata));

            assertEquals("v=spf1", record.getText());
            assertEquals("SPF v=spf1", record.toString());
        }
    }

    @Nested
    class HINFORecordTests {
        @Test
        public void parsesCpuAndOsCharacterStrings() {
            // RDATA: two <character-string>s, each a 1-byte length prefix + that many bytes.
            byte[] rdata = concat(new byte[]{0x05}, ascii("Intel"), new byte[]{0x04}, ascii("Unix"));
            HINFORecord record = new HINFORecord();
            record.parseData((short) rdata.length, new Buffer(rdata));

            assertEquals("HINFO Intel Unix", record.toString());
        }
    }

    @Nested
    class Caa {
        @Test
        public void parsesFlagTagAndValue() {
            // RDATA: flag(1) + tagLength(1) + tag + value
            byte[] rdata = concat(new byte[]{0x00, 0x05}, ascii("issue"), ascii("ca.example.com"));
            CAARecord record = new CAARecord();
            record.parseData((short) rdata.length, new Buffer(rdata));

            assertEquals("CAA issue ca.example.com", record.toString());
        }
    }

    @Nested
    class UnknownRecordTests {
        @Test
        public void knownTypeRendersEnumName() {
            byte[] rdata = {1, 2, 3, 4, 5};
            UnknownRecord record = new UnknownRecord(RecordType.ANY, (short) 255);
            record.parseData((short) rdata.length, new Buffer(rdata));

            assertArrayEquals(rdata, record.getData());
            assertEquals(RecordType.ANY, record.getType());
            assertEquals((short) 255, record.getTypeId());
            assertEquals("ANY", record.toString());
        }

        @Test
        public void unknownTypeRendersNumericName() {
            UnknownRecord record = new UnknownRecord(null, (short) 9999);
            record.parseData((short) 0, new Buffer(new byte[0]));

            assertNull(record.getType());
            assertEquals((short) 9999, record.getTypeId());
            assertEquals("TYPE_9999", record.toString());
        }
    }

    @Nested
    class SRVRecordTests {
        @Test
        public void parsesPriorityWeightPortAndTarget() {
            byte[] rdata = concat(new byte[]{0, 0, 0, 5, 0x01, (byte) 0xBB}, encodeName("host.example.com"));
            SRVRecord record = new SRVRecord();
            record.parseData((short) rdata.length, new Buffer(rdata));

            assertEquals(0, record.getPriority());
            assertEquals(5, record.getWeight());
            assertEquals(443, record.getPort());
            assertEquals("host.example.com.", record.getTarget());
            assertEquals("SRV 0 5 443 host.example.com.", record.toString());
        }
    }

    @Nested
    class KXRecordTests {
        @Test
        public void parsesPreferenceAndExchanger() {
            byte[] rdata = concat(new byte[]{0, 1}, encodeName("kx.example.com"));
            KXRecord record = new KXRecord();
            record.parseData((short) rdata.length, new Buffer(rdata));

            assertEquals(1, record.getPreference());
            assertEquals("KX 1 kx.example.com.", record.toString());
        }
    }

    @Nested
    class DNAMERecordTests {
        @Test
        public void parsesTargetName() {
            byte[] rdata = encodeName("target.example.com");
            DNAMERecord record = new DNAMERecord();
            record.parseData((short) rdata.length, new Buffer(rdata));

            assertEquals("target.example.com.", record.getName());
            assertEquals("DNAME target.example.com.", record.toString());
        }
    }

    @Nested
    class URIRecordTests {
        @Test
        public void parsesPriorityWeightAndQuotedTarget() {
            byte[] rdata = concat(new byte[]{0, 0x0A, 0, 1}, ascii("https://example.com/"));
            URIRecord record = new URIRecord();
            record.parseData((short) rdata.length, new Buffer(rdata));

            assertEquals(10, record.getPriority());
            assertEquals(1, record.getWeight());
            assertEquals("https://example.com/", record.getTarget());
            assertEquals("URI 10 1 \"https://example.com/\"", record.toString());
        }
    }

    @Nested
    class NAPTRRecordTests {
        @Test
        public void parsesOrderPreferenceCharStringsAndReplacement() {
            byte[] rdata = concat(
                    new byte[]{0, 0x64, 0, 0x0A},
                    charString("S"),
                    charString("SIP+D2U"),
                    charString(""),
                    encodeName("_sip._udp.example.com"));
            NAPTRRecord record = new NAPTRRecord();
            record.parseData((short) rdata.length, new Buffer(rdata));

            assertEquals(100, record.getOrder());
            assertEquals(10, record.getPreference());
            assertEquals("S", record.getFlags());
            assertEquals("SIP+D2U", record.getServices());
            assertEquals("", record.getRegexp());
            assertEquals("_sip._udp.example.com.", record.getReplacement());
            assertEquals("NAPTR 100 10 \"S\" \"SIP+D2U\" \"\" _sip._udp.example.com.", record.toString());
        }
    }

    @Nested
    class SSHFPRecordTests {
        @Test
        public void parsesAlgorithmTypeAndHexFingerprint() {
            byte[] rdata = {1, 2, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF};
            SSHFPRecord record = new SSHFPRecord();
            record.parseData((short) rdata.length, new Buffer(rdata));

            assertEquals(1, record.getAlgorithm());
            assertEquals(2, record.getFingerprintType());
            assertEquals("SSHFP 1 2 abcdef", record.toString());
        }
    }

    @Nested
    class TLSARecordTests {
        @Test
        public void parsesUsageSelectorMatchingAndHexData() {
            byte[] rdata = {3, 1, 1, 0x12, 0x34};
            TLSARecord record = new TLSARecord();
            record.parseData((short) rdata.length, new Buffer(rdata));

            assertEquals(3, record.getCertificateUsage());
            assertEquals(1, record.getSelector());
            assertEquals(1, record.getMatchingType());
            assertEquals("TLSA 3 1 1 1234", record.toString());
        }

        @Test
        public void smimeaSharesFormatButRelabels() {
            byte[] rdata = {3, 1, 1, 0x12, 0x34};
            SMIMEARecord record = new SMIMEARecord();
            record.parseData((short) rdata.length, new Buffer(rdata));

            assertEquals("SMIMEA 3 1 1 1234", record.toString());
        }
    }

    @Nested
    class DSRecordTests {
        @Test
        public void parsesKeyTagAlgorithmDigestTypeAndHexDigest() {
            byte[] rdata = {0x30, 0x39, 8, 2, (byte) 0xDE, (byte) 0xAD};
            DSRecord record = new DSRecord();
            record.parseData((short) rdata.length, new Buffer(rdata));

            assertEquals(12345, record.getKeyTag());
            assertEquals(8, record.getAlgorithm());
            assertEquals(2, record.getDigestType());
            assertEquals("DS 12345 8 2 dead", record.toString());
        }

        @Test
        public void cdsSharesFormatButRelabels() {
            byte[] rdata = {0x30, 0x39, 8, 2, (byte) 0xDE, (byte) 0xAD};
            CDSRecord record = new CDSRecord();
            record.parseData((short) rdata.length, new Buffer(rdata));

            assertEquals("CDS 12345 8 2 dead", record.toString());
        }
    }

    @Nested
    class DNSKEYRecordTests {
        @Test
        public void parsesFlagsProtocolAlgorithmAndBase64Key() {
            byte[] rdata = concat(new byte[]{0x01, 0x00, 3, 8}, ascii("Man"));
            DNSKEYRecord record = new DNSKEYRecord();
            record.parseData((short) rdata.length, new Buffer(rdata));

            assertEquals(256, record.getFlags());
            assertEquals(3, record.getProtocol());
            assertEquals(8, record.getAlgorithm());
            assertEquals("DNSKEY 256 3 8 TWFu", record.toString());
        }

        @Test
        public void cdnskeySharesFormatButRelabels() {
            byte[] rdata = concat(new byte[]{0x01, 0x00, 3, 8}, ascii("Man"));
            CDNSKEYRecord record = new CDNSKEYRecord();
            record.parseData((short) rdata.length, new Buffer(rdata));

            assertEquals("CDNSKEY 256 3 8 TWFu", record.toString());
        }
    }

    @Nested
    class EUIRecordTests {
        @Test
        public void eui48RendersHyphenHex() {
            byte[] rdata = {0x00, 0x00, 0x5e, 0x00, 0x53, 0x2a};
            EUI48Record record = new EUI48Record();
            record.parseData((short) rdata.length, new Buffer(rdata));

            assertArrayEquals(rdata, record.getAddress());
            assertEquals("EUI48 00-00-5e-00-53-2a", record.toString());
        }

        @Test
        public void eui64RendersHyphenHex() {
            byte[] rdata = {0x00, 0x00, 0x5e, (byte) 0xef, 0x10, 0x00, 0x00, 0x2a};
            EUI64Record record = new EUI64Record();
            record.parseData((short) rdata.length, new Buffer(rdata));

            assertArrayEquals(rdata, record.getAddress());
            assertEquals("EUI64 00-00-5e-ef-10-00-00-2a", record.toString());
        }
    }

    @Nested
    class RRSIGRecordTests {
        @Test
        public void parsesFixedFieldsSignerNameAndBase64Signature() {
            byte[] rdata = concat(
                    u16(1),                 // type covered = A
                    new byte[]{8, 2},       // algorithm = 8, labels = 2
                    u32(86400),             // original TTL
                    u32(1735689600L),       // expiration -> 20250101000000 UTC
                    u32(1704067200L),       // inception  -> 20240101000000 UTC
                    u16(12345),             // key tag
                    encodeName("example.com"),
                    ascii("Man"));          // signature -> base64 "TWFu"
            RRSIGRecord record = new RRSIGRecord();
            record.parseData((short) rdata.length, new Buffer(rdata));

            assertEquals(1, record.getTypeCovered());
            assertEquals(8, record.getAlgorithm());
            assertEquals(12345, record.getKeyTag());
            assertEquals("example.com.", record.getSignerName());
            assertEquals("RRSIG A 8 2 86400 20250101000000 20240101000000 12345 example.com. TWFu",
                    record.toString());
        }
    }

    @Nested
    class NSECRecordTests {
        @Test
        public void parsesNextNameAndTypeBitmap() {
            // Bitmap window 0, length 6: bits set for A(1), NS(2), RRSIG(46).
            byte[] rdata = concat(
                    encodeName("next.example.com"),
                    new byte[]{0x00, 0x06, 0x60, 0x00, 0x00, 0x00, 0x00, 0x02});
            NSECRecord record = new NSECRecord();
            record.parseData((short) rdata.length, new Buffer(rdata));

            assertEquals("next.example.com.", record.getNextDomainName());
            assertEquals(java.util.Arrays.asList(1, 2, 46), record.getTypes());
            assertEquals("NSEC next.example.com. A NS RRSIG", record.toString());
        }
    }

    @Nested
    class NSEC3RecordTests {
        @Test
        public void parsesParametersSaltOwnerAndBitmap() {
            byte[] rdata = concat(
                    new byte[]{1, 1},                          // hash algorithm = 1, flags = 1
                    u16(0),                                    // iterations
                    new byte[]{0x02, (byte) 0xAB, (byte) 0xCD},// salt length 2 + salt
                    new byte[]{0x06}, ascii("foobar"),         // hash length 6 + next hashed owner
                    new byte[]{0x00, 0x06, 0x60, 0x00, 0x00, 0x00, 0x00, 0x02});
            NSEC3Record record = new NSEC3Record();
            record.parseData((short) rdata.length, new Buffer(rdata));

            assertEquals(1, record.getHashAlgorithm());
            assertEquals(0, record.getIterations());
            // salt -> lowercase hex; next hashed owner -> base32hex of "foobar".
            assertEquals("NSEC3 1 1 0 abcd CPNMUOJ1E8 A NS RRSIG", record.toString());
        }

        @Test
        public void rendersEmptySaltAsDash() {
            byte[] rdata = concat(
                    new byte[]{1, 0},          // hash algorithm = 1, flags = 0
                    u16(10),                   // iterations
                    new byte[]{0x00},          // salt length 0
                    new byte[]{0x01, 0x00});   // hash length 1 + 1-byte owner; no type bitmap
            NSEC3Record record = new NSEC3Record();
            record.parseData((short) rdata.length, new Buffer(rdata));

            assertEquals("NSEC3 1 0 10 - 00", record.toString());
        }
    }

    @Nested
    class SVCBRecordTests {
        private byte[] fixture() {
            byte[] alpn = concat(charString("h3"), charString("h2"));
            return concat(
                    u16(1),                                       // SvcPriority
                    encodeName("svc.example.net"),                // TargetName
                    u16(1), u16(alpn.length), alpn,               // alpn="h3,h2"
                    u16(3), u16(2), new byte[]{0x01, (byte) 0xBB},// port=443
                    u16(4), u16(4), new byte[]{(byte) 192, 0, 2, 1}); // ipv4hint=192.0.2.1
        }

        @Test
        public void parsesPriorityTargetAndSvcParams() {
            byte[] rdata = fixture();
            SVCBRecord record = new SVCBRecord();
            record.parseData((short) rdata.length, new Buffer(rdata));

            assertEquals(1, record.getSvcPriority());
            assertEquals("svc.example.net.", record.getTargetName());
            assertEquals(3, record.getParams().size());
            assertEquals("SVCB 1 svc.example.net. alpn=\"h3,h2\" port=443 ipv4hint=192.0.2.1",
                    record.toString());
        }

        @Test
        public void httpsSharesFormatButRelabels() {
            byte[] rdata = fixture();
            HTTPSRecord record = new HTTPSRecord();
            record.parseData((short) rdata.length, new Buffer(rdata));

            assertEquals("HTTPS 1 svc.example.net. alpn=\"h3,h2\" port=443 ipv4hint=192.0.2.1",
                    record.toString());
        }
    }

    @Nested
    class RecordDataTests {
        @Test
        public void base64MatchesKnownVectors() {
            assertEquals("TWFu", RecordData.toBase64(ascii("Man")));
            assertEquals("TWE=", RecordData.toBase64(ascii("Ma")));
            assertEquals("TQ==", RecordData.toBase64(ascii("M")));
            assertEquals("", RecordData.toBase64(new byte[0]));
        }

        @Test
        public void hexFormatsLowercaseWithOptionalSeparator() {
            byte[] data = {0x00, (byte) 0xab, (byte) 0xff};
            assertEquals("00abff", RecordData.toHex(data));
            assertEquals("00-ab-ff", RecordData.toHex(data, '-'));
        }

        @Test
        public void base32HexMatchesRfc4648Vector() {
            assertEquals("CPNMUOJ1E8", RecordData.toBase32Hex(ascii("foobar")));
            assertEquals("", RecordData.toBase32Hex(new byte[0]));
        }

        @Test
        public void sigTimeFormatsEpochSecondsAsUtc() {
            assertEquals("20250101000000", RecordData.sigTime(1735689600L));
        }

        @Test
        public void bitmapTypesDecodesWindows() {
            byte[] bitmap = {0x00, 0x06, 0x60, 0x00, 0x00, 0x00, 0x00, 0x02};
            assertEquals(java.util.Arrays.asList(1, 2, 46), RecordData.bitmapTypes(bitmap));
        }
    }
}
