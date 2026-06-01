package com.kendamasoft.dns;

import com.kendamasoft.dns.protocol.Header;
import com.kendamasoft.dns.protocol.Message;
import com.kendamasoft.dns.protocol.MessageBuilder;
import com.kendamasoft.dns.protocol.RecordType;
import com.kendamasoft.dns.records.ARecord;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the public {@link DnsConnection#doRequest(Message)} template method offline by
 * subclassing {@code DnsConnection} and overriding the protected transport hooks to return
 * canned wire bytes — no network involved — plus the connection constructors' input validation.
 */
public class DnsConnectionUnitTest {

    /** Test transport: captures the serialized request and replays a canned response. */
    private static final class CannedConnection extends DnsConnection {
        private final byte[] response;
        byte[] sentRequest;

        CannedConnection(byte[] response) {
            this.response = response;
        }

        @Override
        protected void send(byte[] request) {
            this.sentRequest = request;
        }

        @Override
        protected byte[] receive() {
            return response;
        }
    }

    private static Message request() {
        return new MessageBuilder().setName("example.com").setType(RecordType.A).build();
    }

    @Test
    public void doRequestSerializesRequestAndParsesResponse() throws IOException {
        CannedConnection connection = new CannedConnection(successResponse());

        Message response = connection.doRequest(request());

        assertNotNull(connection.sentRequest);
        assertTrue(connection.sentRequest.length > 12);   // header (12) + question were serialized
        assertEquals(1, response.getAnswerRecordList().size());
        ARecord a = (ARecord) response.getAnswerRecordList().get(0).getContent();
        assertEquals("A 93.184.216.34", a.toString());
    }

    @Test
    public void doRequestThrowsOnNonZeroReturnCode() {
        CannedConnection connection = new CannedConnection(errorResponse());
        assertThrows(IOException.class, () -> connection.doRequest(request()));
    }

    @Test
    public void truncatedResponseExposesTruncationFlag() throws IOException {
        CannedConnection connection = new CannedConnection(truncatedResponse());
        Message response = connection.doRequest(request());
        assertTrue(response.getHeader().hasFlag(Header.FLAG_TRUNCATION));
    }

    @Nested
    class ConstructorValidation {

        @Test
        public void dohRejectsNonHttpsUrl() {
            assertThrows(IllegalArgumentException.class,
                    () -> new DnsConnectionDoh("http://1.1.1.1/dns-query"));
        }

        @Test
        public void dohAcceptsHttpsUrl() {
            assertDoesNotThrow(() -> new DnsConnectionDoh("https://1.1.1.1/dns-query"));
        }

        @Test
        public void udpRejectsNullHost() {
            assertThrows(NullPointerException.class, () -> new DnsConnectionUdp(null));
        }

        @Test
        public void tcpRejectsNullHost() {
            assertThrows(NullPointerException.class, () -> new DnsConnectionTcp(null));
        }
    }

    // ---- canned wire fixtures ----

    /** "example.com" A response, rcode 0, one answer (93.184.216.34). */
    private static byte[] successResponse() {
        return new byte[]{
                0x12, 0x34, (byte) 0x81, (byte) 0x80, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00,
                0x07, 'e', 'x', 'a', 'm', 'p', 'l', 'e', 0x03, 'c', 'o', 'm', 0x00, 0x00, 0x01, 0x00, 0x01,
                (byte) 0xC0, 0x0C, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00, 0x0E, 0x10, 0x00, 0x04,
                93, (byte) 184, (byte) 216, 34
        };
    }

    /** flags 0x8183 => response + rcode 3 (name error); zero answers. */
    private static byte[] errorResponse() {
        return new byte[]{
                0x12, 0x34, (byte) 0x81, (byte) 0x83, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x07, 'e', 'x', 'a', 'm', 'p', 'l', 'e', 0x03, 'c', 'o', 'm', 0x00, 0x00, 0x01, 0x00, 0x01
        };
    }

    /** flags 0x8200 => response + truncation, rcode 0 (readMessage returns after the question). */
    private static byte[] truncatedResponse() {
        return new byte[]{
                0x12, 0x34, (byte) 0x82, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x07, 'e', 'x', 'a', 'm', 'p', 'l', 'e', 0x03, 'c', 'o', 'm', 0x00, 0x00, 0x01, 0x00, 0x01
        };
    }
}
