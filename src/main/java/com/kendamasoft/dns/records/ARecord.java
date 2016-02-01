package com.kendamasoft.dns.records;

import com.kendamasoft.dns.protocol.Buffer;

/**
 * IPv4 address record
 */
public class ARecord extends AbstractRecord {

    final byte[] address = new byte[4];

    @Override
    public void parseData(short dataLength, Buffer buffer) {
        for(int i=0; i<4; i++) {
            address[i] = buffer.readByte();
        }
    }

    public byte[] getAddress() {
        return address;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(byte b : address) {
            sb.append(b & 0xff).append(".");
        }
        sb.delete(sb.length() - 1, sb.length());
        return "A " + sb.toString();
    }
}
