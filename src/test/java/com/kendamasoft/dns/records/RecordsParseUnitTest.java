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
 * behavior (including the known {@code CAARecord} "CCA" typo — see {@link Caa}).
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
    class Caa {
        @Test
        public void parsesFlagTagAndValue() {
            // RDATA: flag(1) + tagLength(1) + tag + value
            byte[] rdata = concat(new byte[]{0x00, 0x05}, ascii("issue"), ascii("ca.example.com"));
            CAARecord record = new CAARecord();
            record.parseData((short) rdata.length, new Buffer(rdata));

            // BUG (pinned): CAARecord.toString() emits "CCA" — a typo for "CAA".
            assertEquals("CCA issue ca.example.com", record.toString());
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
}
