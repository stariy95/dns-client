package com.kendamasoft.dns;

import com.kendamasoft.dns.protocol.Header;
import com.kendamasoft.dns.protocol.Message;

import java.io.IOException;

/**
 * Connection with auto fallback from UDP to TCP
 */
public class DnsConnectionAuto extends DnsConnection {

    public Message doRequest(Message request) throws IOException {
        Message response = new DnsConnectionUdp().doRequest(request);
        if (response.getHeader().hasFlag(Header.FLAG_TRUNCATION)) {
            response = new DnsConnectionTcp().doRequest(request);
        }
        return response;
    }

    @Override
    protected void send(byte[] request) throws IOException {
    }

    @Override
    protected byte[] receive() throws IOException {
        return null;
    }
}
