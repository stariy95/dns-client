package com.kendamasoft.dns;

/**
 * Connection to DNS server<br>
 *
 * @see ConnectionUdp
 * @see ConnectionTcp
 */
abstract class Connection {

    /**
     * Default DNS server port
     */
    public static final int DNS_PORT = 53;

    static final int SOCKET_TIMEOUT_MS = 5000;

    static final byte[] googleDnsAddress = {8, 8, 8, 8};

    public DnsProtocol.Message doRequest(DnsProtocol.Message request) throws Exception {
        Buffer buffer = new Buffer();
        buffer.write(request);

        send(buffer.getData());
        byte data[] = receive();

        buffer = new Buffer(data);
        return buffer.readMessage();
    }

    protected abstract void send(byte[] request) throws Exception;
    protected abstract byte[] receive() throws Exception;
}
