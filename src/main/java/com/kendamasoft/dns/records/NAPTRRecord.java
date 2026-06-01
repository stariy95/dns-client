package com.kendamasoft.dns.records;

import com.kendamasoft.dns.protocol.Buffer;

/**
 * Naming Authority Pointer record (RFC 3403)
 * @since 1.2.0
 */
public class NAPTRRecord extends AbstractRecord {

    int order;

    int preference;

    String flags;

    String services;

    String regexp;

    String replacement;

    @Override
    public void parseData(short dataLength, Buffer buffer) {
        order       = buffer.readShort() & 0xFFFF;
        preference  = buffer.readShort() & 0xFFFF;
        flags       = RecordData.characterString(buffer);
        services    = RecordData.characterString(buffer);
        regexp      = RecordData.characterString(buffer);
        replacement = buffer.readString();
    }

    public int getOrder() {
        return order;
    }

    public int getPreference() {
        return preference;
    }

    public String getFlags() {
        return flags;
    }

    public String getServices() {
        return services;
    }

    public String getRegexp() {
        return regexp;
    }

    public String getReplacement() {
        return replacement;
    }

    @Override
    public String toString() {
        return "NAPTR " + order + " " + preference
                + " \"" + flags + "\" \"" + services + "\" \"" + regexp + "\" " + replacement;
    }
}
