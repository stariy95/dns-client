package com.kendamasoft.dns;

import com.kendamasoft.dns.protocol.Buffer;
import com.kendamasoft.dns.protocol.Header;
import com.kendamasoft.dns.protocol.Message;

import java.io.IOException;

/**
 * Connection to DNS server<br>
 *
 * @see DnsConnectionUdp
 * @see DnsConnectionTcp
 */
public abstract class DnsConnection {

    /**
     * Default DNS server port
     */
    public static final int DNS_PORT = 53;

    public static final int MAX_MESSAGE_LENGTH = 512;

    static final int SOCKET_TIMEOUT_MS = 5000;

    static final byte[] googleDnsAddress = {8, 8, 8, 8};

    public Message doRequest(Message request) throws IOException {
        Buffer buffer = new Buffer();
        buffer.write(request);

        send(buffer.getData());
        byte[] data = receive();

        buffer = new Buffer(data);
        Message response = buffer.readMessage();
        if(response.getHeader().returnCode() != 0) {
            throw new IOException("Got response message error code: "
                    + errorCodeName(response.getHeader().returnCode()));
        }
        return response;
    }

    private static String errorCodeName(int errorCode) {
        String name = " ";
        switch (errorCode) {
            case Header.ERROR_FORMAT:
                name = "Format Error";
                break;
            case Header.ERROR_SERVER_FAILURE:
                name = "Server Failure";
                break;
            case Header.ERROR_NAME:
                name = "Name Error";
                break;
            case Header.ERROR_NOT_IMPLEMENTED:
                name = "Not Implemented";
                break;
            case Header.ERROR_REFUSED:
                name = "Refused";
                break;
        }
        return name + String.format("(0x%01X)", errorCode);
    }

    protected abstract void send(byte[] request) throws IOException;
    protected abstract byte[] receive() throws IOException;
}
