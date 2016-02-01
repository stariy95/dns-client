package com.kendamasoft.dns.records;

import com.kendamasoft.dns.protocol.Buffer;

/**
 * Pointer record
 */
public class PTRRecord extends AbstractRecord {

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
        return "PTR " + name;
    }
}
