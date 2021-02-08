package com.kendamasoft.dns.records;

import com.kendamasoft.dns.protocol.Buffer;

/**
 * A Canonical Name record
 * @since 1.1.0
 */
public class CNAMERecord extends AbstractRecord {

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
        return "CNAME " + name;
    }

}
