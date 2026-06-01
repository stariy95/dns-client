package com.kendamasoft.dns.records;

import com.kendamasoft.dns.protocol.Buffer;

/**
 * Key Exchanger record (RFC 2230)
 * @since 1.2.0
 */
public class KXRecord extends AbstractRecord {

    int preference;

    String name;

    @Override
    public void parseData(short dataLength, Buffer buffer) {
        preference = buffer.readShort() & 0xFFFF;
        name = buffer.readString();
    }

    public int getPreference() {
        return preference;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "KX " + preference + " " + name;
    }
}
