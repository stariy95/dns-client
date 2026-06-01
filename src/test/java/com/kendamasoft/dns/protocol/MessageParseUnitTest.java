package com.kendamasoft.dns.protocol;

import com.kendamasoft.dns.records.ARecord;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests response parsing of the public model ({@link Message} / {@link Header} /
 * {@link ResourceRecord} / record content) from canned DNS wire-format bytes.
 *
 * <p>A {@code Message} carrying answer records has no public constructor, so fixtures are
 * raw response bytes parsed via {@link Buffer#readMessage()} (the public codec entry point).
 */
public class MessageParseUnitTest {

    /** Response for an "example.com" A query, answer name given as a compression pointer. */
    private static byte[] aResponse() {
        return new byte[]{
                0x12, 0x34,                                                 // transaction id
                (byte) 0x81, (byte) 0x80,                                   // flags: QR, RD, RA, rcode 0
                0x00, 0x01,                                                 // qdcount = 1
                0x00, 0x01,                                                 // ancount = 1
                0x00, 0x00,                                                 // nscount = 0
                0x00, 0x00,                                                 // arcount = 0
                0x07, 'e', 'x', 'a', 'm', 'p', 'l', 'e',                    // question: example
                0x03, 'c', 'o', 'm', 0x00,                                 //           .com.
                0x00, 0x01, 0x00, 0x01,                                     // qtype A, qclass IN
                (byte) 0xC0, 0x0C,                                          // answer name -> pointer to offset 12
                0x00, 0x01, 0x00, 0x01,                                     // type A, class IN
                0x00, 0x00, 0x0E, 0x10,                                     // ttl = 3600
                0x00, 0x04,                                                 // rdlength = 4
                93, (byte) 184, (byte) 216, 34                             // 93.184.216.34
        };
    }

    /** Two answers in AAAA-then-A wire order, to verify getAllRecords() sorts by type id. */
    private static byte[] twoAnswerResponse() {
        return new byte[]{
                0x12, 0x34, (byte) 0x81, (byte) 0x80, 0x00, 0x01, 0x00, 0x02, 0x00, 0x00, 0x00, 0x00,
                0x07, 'e', 'x', 'a', 'm', 'p', 'l', 'e', 0x03, 'c', 'o', 'm', 0x00, 0x00, 0x01, 0x00, 0x01,
                // answer 1: AAAA (type 28)
                (byte) 0xC0, 0x0C, 0x00, 0x1C, 0x00, 0x01, 0x00, 0x00, 0x0E, 0x10, 0x00, 0x10,
                0x20, 0x01, 0x0d, (byte) 0xb8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0x01,
                // answer 2: A (type 1)
                (byte) 0xC0, 0x0C, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00, 0x0E, 0x10, 0x00, 0x04,
                93, (byte) 184, (byte) 216, 34
        };
    }

    @Test
    public void parsesHeaderFlagsAndReturnCode() {
        Header header = new Buffer(aResponse()).readMessage().getHeader();

        assertTrue(header.hasFlag(Header.FLAG_MESSAGE_TYPE));
        assertTrue(header.hasFlag(Header.FLAG_RECURSION_AVAILABLE));
        assertTrue(header.hasFlag(Header.FLAG_RECURSION_DESIRED));
        assertFalse(header.hasFlag(Header.FLAG_TRUNCATION));
        assertEquals(0, header.returnCode());
        assertEquals(1, header.getAnswerResourceRecordsCount());
    }

    @Test
    public void parsesAnswerRecordIncludingCompressedName() {
        List<ResourceRecord> answers = new Buffer(aResponse()).readMessage().getAnswerRecordList();

        assertEquals(1, answers.size());
        ResourceRecord record = answers.get(0);
        assertEquals("example.com.", record.getName());           // resolved via the 0xC00C pointer
        assertEquals(RecordType.A, record.getRecordType());
        assertEquals(3600, record.getTtl());

        assertInstanceOf(ARecord.class, record.getContent());
        ARecord a = (ARecord) record.getContent();
        assertArrayEquals(new byte[]{93, (byte) 184, (byte) 216, 34}, a.getAddress());
        assertEquals("A 93.184.216.34", a.toString());
    }

    @Test
    public void getAllRecordsSortsByTypeId() {
        List<ResourceRecord> all = new Buffer(twoAnswerResponse()).readMessage().getAllRecords();

        assertEquals(2, all.size());
        assertEquals(RecordType.A, all.get(0).getRecordType());      // id 1 before
        assertEquals(RecordType.AAAA, all.get(1).getRecordType());   // id 28
    }

    @Test
    public void recordListsAreUnmodifiable() {
        Message message = new Buffer(aResponse()).readMessage();

        List<ResourceRecord> answers = message.getAnswerRecordList();
        assertThrows(UnsupportedOperationException.class, () -> answers.add(null));
        assertTrue(message.getAdditionalRecordList().isEmpty());
    }
}
