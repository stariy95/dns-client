package com.kendamasoft.dns.records;

import com.kendamasoft.dns.protocol.Buffer;

/**
 * Delegation Name record (RFC 6672)
 * @since 1.2.0
 */
public class DNAMERecord extends AbstractRecord {

    String name;

    @Override
    public void parseData(short dataLength, Buffer buffer) {
        name = buffer.readString();
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "DNAME " + name;
    }
}
