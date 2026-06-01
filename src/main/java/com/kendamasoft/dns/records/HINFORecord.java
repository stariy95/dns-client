package com.kendamasoft.dns.records;

import com.kendamasoft.dns.protocol.Buffer;

/**
 *
 */
public class HINFORecord extends AbstractRecord {

    String CPU;

    String OS;

    @Override
    public void parseData(short dataLength, Buffer buffer) {
        // HINFO RDATA is two <character-string>s (RFC 1035 3.3.2): each is a 1-byte
        // length prefix followed by that many bytes - not a compressed domain name.
        CPU = readCharacterString(buffer);
        OS = readCharacterString(buffer);
    }

    private static String readCharacterString(Buffer buffer) {
        int length = buffer.readByte() & 0xff;
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append((char)(buffer.readByte() & 0xff));
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "HINFO " + CPU + " " + OS;
    }
}
