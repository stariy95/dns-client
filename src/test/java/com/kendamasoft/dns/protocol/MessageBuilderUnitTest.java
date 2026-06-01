package com.kendamasoft.dns.protocol;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the public {@link MessageBuilder} request-building API.
 *
 * <p>A built request's question (name/type) is not observable through any public getter,
 * so it is verified here at the wire-byte level via {@link Buffer#write(Message)} +
 * {@link Buffer#getData()} — the same serialization {@code DnsConnection.doRequest()} performs.
 */
public class MessageBuilderUnitTest {

    private static byte[] serialize(Message message) {
        Buffer buffer = new Buffer();
        buffer.write(message);
        return buffer.getData();
    }

    @Test
    public void buildSetsRecursionDesiredAndEmptyRecordLists() {
        Message message = new MessageBuilder().setName("example.com").build();

        Header header = message.getHeader();
        assertTrue(header.hasFlag(Header.FLAG_RECURSION_DESIRED));
        assertEquals(0, header.opCode());
        assertEquals(0, header.returnCode());

        assertTrue(message.getAnswerRecordList().isEmpty());
        assertTrue(message.getAuthorityRecordList().isEmpty());
        assertTrue(message.getAdditionalRecordList().isEmpty());
        assertTrue(message.getAllRecords().isEmpty());
    }

    @Test
    public void buildEncodesSingleQuestionOnTheWire() {
        byte[] wire = serialize(new MessageBuilder().setName("example.com").build());

        // header: flags (recursion desired = 0x0100), qdcount = 1, an/ns/ar counts = 0
        assertEquals(0x01, wire[2]);
        assertEquals(0x00, wire[3]);
        assertEquals(0x00, wire[4]);
        assertEquals(0x01, wire[5]);
        for (int i = 6; i < 12; i++) {
            assertEquals(0, wire[i]);
        }

        // question name "example.com" as length-prefixed labels terminated by a zero octet
        int p = 12;
        assertEquals(7, wire[p++]);
        assertEquals("example", new String(wire, p, 7, StandardCharsets.US_ASCII));
        p += 7;
        assertEquals(3, wire[p++]);
        assertEquals("com", new String(wire, p, 3, StandardCharsets.US_ASCII));
        p += 3;
        assertEquals(0, wire[p++]);

        // qtype = A (1), qclass = IN (1)
        assertEquals(0, wire[p++]);
        assertEquals(RecordType.A.getId(), wire[p++]);
        assertEquals(0, wire[p++]);
        assertEquals(1, wire[p]);
    }

    @Test
    public void defaultTypeIsAAndSetTypeIsHonored() {
        byte[] defaulted = serialize(new MessageBuilder().setName("example.com").build());
        byte[] mx = serialize(new MessageBuilder().setName("example.com").setType(RecordType.MX).build());

        // qtype low byte sits right after the 13-byte encoded name (offsets 12..24)
        int qtypeLow = 26;
        assertEquals(RecordType.A.getId(), defaulted[qtypeLow]);
        assertEquals(RecordType.MX.getId(), mx[qtypeLow]);
    }

    @Test
    public void transactionIdAutoIncrements() {
        byte[] first = serialize(new MessageBuilder().setName("example.com").build());
        byte[] second = serialize(new MessageBuilder().setName("example.com").build());

        int id1 = ((first[0] & 0xff) << 8) | (first[1] & 0xff);
        int id2 = ((second[0] & 0xff) << 8) | (second[1] & 0xff);
        assertNotEquals(id1, id2);
    }
}
