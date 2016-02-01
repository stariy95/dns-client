package com.kendamasoft.dns.records;

import com.kendamasoft.dns.protocol.Buffer;

/**
 * Mail exchange record
 */
public class MXRecord extends AbstractRecord {

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
