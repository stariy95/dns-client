package com.kendamasoft.dns.records;

import com.kendamasoft.dns.Buffer;
import com.kendamasoft.dns.Record;

import java.net.InetAddress;

/**
 * IPv6 address record
 */
public class AAAARecord extends Record {

    final byte[] address = new byte[16];

    @Override
    public void parseData(short dataLength, Buffer buffer) {
        for(int i=0; i<16; i++) {
            address[i] = buffer.readByte();
        }
    }

    public byte[] getAddress() {
        return address;
    }

    private String formatAddress() {
        try {
            return InetAddress.getByAddress(address).getHostAddress();
        } catch (Exception ex) {
            return "";
        }
    }

    @Override
    public String toString() {
        return "AAAA " + formatAddress();
    }
}
