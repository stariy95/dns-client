package com.kendamasoft.dns.records;

import com.kendamasoft.dns.Buffer;
import com.kendamasoft.dns.Record;

/**
 * Start of [a zone of] authority record
 */
public class SOARecord extends Record {

    String mName;

    String rName;

    long serial;

    long refresher;

    long retry;

    long expire;

    long minimum;

    @Override
    public void parseData(short dataLength, Buffer buffer) {
        mName = buffer.readString();
        rName = buffer.readString();
        serial = buffer.readUint32();
        refresher = buffer.readUint32();
        retry = buffer.readUint32();
        expire = buffer.readUint32();
        minimum = buffer.readUint32();
    }

    @Override
    public String toString() {
        return "SOA " + mName + " " + rName + " " + serial + " " + refresher + " " + retry + " " + expire + " " + minimum;
    }
}
