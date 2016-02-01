package com.kendamasoft.dns;

//import android.util.Log;

import com.kendamasoft.dns.protocol.DnsProtocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * UDP connection implementation
 */
public class DnsConnectionUdp extends DnsConnection {

    private InetAddress dns;
    private DatagramSocket socket;

    public DnsConnectionUdp() {
        try {
            dns = InetAddress.getByAddress(googleDnsAddress);
            initSocket();
        } catch (Exception ex) {
            ex.printStackTrace();
            // @todo Log.w(TAG, ex);
        }
    }

    public DnsConnectionUdp(InetAddress dnsHost) {
        if(dnsHost == null) {
            throw new NullPointerException("dnsHost is null");
        }
        dns = dnsHost;
        initSocket();
    }

    private void initSocket() {
        try {
            socket = new DatagramSocket();
        } catch (Exception ex) {
            ex.printStackTrace();
            // @todo Log.w(TAG, ex);
        }
    }

    @Override
    protected void send(byte[] request) throws IOException {
        if(socket == null) {
            throw new IllegalStateException("Connection not open");
        }
        if(dns == null) {
            throw new IllegalStateException("Dns server not defined");
        }
        socket.setSoTimeout(SOCKET_TIMEOUT_MS);
        DatagramPacket packet = new DatagramPacket(request, request.length, dns, DNS_PORT);
        socket.send(packet);
    }

    @Override
    protected byte[] receive() throws IOException {
        if(socket == null) {
            throw new IllegalStateException("Connection not open");
        }
        byte[] data = new byte[DnsConnection.MAX_MESSAGE_LENGTH];
        try {
            DatagramPacket packet = new DatagramPacket(data, data.length);
            socket.receive(packet);
        } finally {
            socket.close();
        }
        return data;
    }

}
