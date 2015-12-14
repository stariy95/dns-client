package com.kendamasoft.dns.records;

import com.kendamasoft.dns.Buffer;
import com.kendamasoft.dns.Record;

/**
 * Name server record
 */
public class NSRecord extends Record {

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
        return "NS " + name;
    }
}
