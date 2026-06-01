package com.kendamasoft.dns.records;

import com.kendamasoft.dns.protocol.Buffer;

/**
 * Service locator record (RFC 2782)
 * @since 1.2.0
 */
public class SRVRecord extends AbstractRecord {

    int priority;

    int weight;

    int port;

    String target;

    @Override
    public void parseData(short dataLength, Buffer buffer) {
        priority = buffer.readShort() & 0xFFFF;
        weight   = buffer.readShort() & 0xFFFF;
        port     = buffer.readShort() & 0xFFFF;
        target   = buffer.readString();
    }

    public int getPriority() {
        return priority;
    }

    public int getWeight() {
        return weight;
    }

    public int getPort() {
        return port;
    }

    public String getTarget() {
        return target;
    }

    @Override
    public String toString() {
        return "SRV " + priority + " " + weight + " " + port + " " + target;
    }
}
