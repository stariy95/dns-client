package com.kendamasoft.dns.records;

import com.kendamasoft.dns.Buffer;
import com.kendamasoft.dns.Record;

/**
 * Mail exchange record
 */
public class MXRecord extends Record {

    int priority;

    String name;

    @Override
    public void parseData(short dataLength, Buffer buffer) {
        priority = buffer.readShort() & 0xFFFF;
        name = buffer.readString();
    }

    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public String toString() {
        return "MX " + priority + " " + name;
    }
}
