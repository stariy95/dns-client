package com.kendamasoft.dns.records;

import com.kendamasoft.dns.protocol.Buffer;

import java.nio.charset.StandardCharsets;

/**
 * Uniform Resource Identifier record (RFC 7553).
 * <p>
 * The target is the remaining RDATA as text - it is neither a domain name nor a length-prefixed
 * character-string.
 * @since 1.2.0
 */
public class URIRecord extends AbstractRecord {

    int priority;

    int weight;

    String target;

    @Override
    public void parseData(short dataLength, Buffer buffer) {
        priority = buffer.readShort() & 0xFFFF;
        weight   = buffer.readShort() & 0xFFFF;
        target   = new String(RecordData.remaining(buffer, dataLength - 4), StandardCharsets.US_ASCII);
    }

    public int getPriority() {
        return priority;
    }

    public int getWeight() {
        return weight;
    }

    public String getTarget() {
        return target;
    }

    @Override
    public String toString() {
        return "URI " + priority + " " + weight + " \"" + target + "\"";
    }
}
