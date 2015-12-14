package com.kendamasoft.dns;

//import android.util.Log;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * TCP connection implementation
 */
public class ConnectionTcp extends Connection {

    private SocketAddress dns;
    private Socket socket;

    public ConnectionTcp() {
        try {
            dns = new InetSocketAddress(InetAddress.getByAddress(googleDnsAddress), DNS_PORT);
            initSocket();
        } catch (Exception ex) {
            // @todo Log.w(TAG, ex);
        }
    }

    public ConnectionTcp(InetAddress dnsHost) {
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

    protected void send(byte[] request) throws Exception {
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

    protected byte[] receive() throws Exception {
        if(socket == null || !socket.isConnected()) {
            throw new IllegalStateException("Connection not open");
        }
        try {
            byte[] lengthData = new byte[2];
            int read = socket.getInputStream().read(lengthData);
            if(read == -1) {
                return null;
            }
            int length = (lengthData[0]&0xff) << 8 | (lengthData[1]&0xff);
            byte[] data = new byte[length];
            //noinspection ResultOfMethodCallIgnored
            socket.getInputStream().read(data);
            return data;
        } finally {
            socket.close();
        }
    }
}
