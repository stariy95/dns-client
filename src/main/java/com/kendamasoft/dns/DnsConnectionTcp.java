package com.kendamasoft.dns;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * TCP connection implementation
 */
public class DnsConnectionTcp extends DnsConnection {

    private SocketAddress dns;
    private Socket socket;

    public DnsConnectionTcp() {
        try {
            dns = new InetSocketAddress(InetAddress.getByAddress(googleDnsAddress), DNS_PORT);
            initSocket();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public DnsConnectionTcp(InetAddress dnsHost) {
        if(dnsHost == null) {
            throw new NullPointerException("dnsHost is null");
        }
        dns = new InetSocketAddress(dnsHost, DNS_PORT);
        initSocket();
    }

    private void initSocket() {
        try {
            socket = new Socket();
            socket.setSoTimeout(SOCKET_TIMEOUT_MS);
            socket.connect(dns, SOCKET_TIMEOUT_MS);
        } catch (Exception ex) {
            //@todo Log.w(TAG, ex);
        }
    }

    protected void send(byte[] request) throws IOException {
        if(socket == null) {
            throw new IllegalStateException("Connection not open");
        }
        if(dns == null) {
            throw new IllegalStateException("Dns server not defined");
        }
        byte[] length = new byte[2];
        length[0] = (byte)(request.length << 8);
        length[1] = (byte)(request.length);
        socket.getOutputStream().write(length);
        socket.getOutputStream().write(request);
    }

    protected byte[] receive() throws IOException {
        if(socket == null || !socket.isConnected()) {
            throw new IllegalStateException("Connection not open");
        }
        try {
            int expectedLength = readLength();
            byte[] data = new byte[expectedLength];
            int readLength = readFullBuffer(data);
            if(readLength != expectedLength) {
                throw new IOException("Unable to read all message data. Try again.");
            }
            return data;
        } finally {
            socket.close();
        }
    }

    private int readLength() throws IOException {
        byte[] lengthBuf = new byte[2];
        int lengthBufRead = readFullBuffer(lengthBuf);
        if (lengthBufRead < 2) {
            throw new IOException("Unable to read message length. Try resend message or connect to different server.");
        }
        int length = (lengthBuf[0] & 0xff) << 8 | (lengthBuf[1] & 0xff);
        if(length <= 0) {
            throw new IOException("Unable to read message length. Try resend message or connect to different server.");
        }
        return length;
    }

    private int readFullBuffer(byte[] buffer) throws IOException {
        int bufLength = buffer.length;
        int bufRead = 0;
        int read;
        do {
            read = socket.getInputStream().read(buffer, bufRead, bufLength - bufRead);
            bufRead += read;
        } while (read != -1 && bufRead < bufLength);
        return bufRead;
    }
}
