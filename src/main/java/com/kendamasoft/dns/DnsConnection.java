package com.kendamasoft.dns;

import java.io.IOException;

/**
 * Connection to DNS server<br>
 *
 * @see DnsConnectionUdp
 * @see DnsConnectionTcp
 */
abstract class DnsConnection {

    /**
     * Default DNS server port
     */
    public static final int DNS_PORT = 53;

    static final int SOCKET_TIMEOUT_MS = 5000;

    static final byte[] googleDnsAddress = {8, 8, 8, 8};

    public DnsProtocol.Message doRequest(DnsProtocol.Message request) throws IOException {
        Buffer buffer = new Buffer();
        buffer.write(request);

        send(buffer.getData());
        byte data[] = receive();

        buffer = new Buffer(data);
        DnsProtocol.Message response = buffer.readMessage();
        if(response.header.returnCode() != 0) {
            throw new IOException("Got response message error code: " + String.format("0x%01X", response.header.returnCode()));
        }
        return response;
    }

    protected abstract void send(byte[] request) throws IOException;
    protected abstract byte[] receive() throws IOException;
}