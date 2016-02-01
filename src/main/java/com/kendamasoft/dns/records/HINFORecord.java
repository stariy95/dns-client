package com.kendamasoft.dns.records;

import com.kendamasoft.dns.protocol.Buffer;

/**
 *
 */
public class HINFORecord extends AbstractRecord {

    String CPU;

    String OS;

    @Override
    public void parseData(short dataLength, Buffer buffer) {
        CPU = buffer.readString();
        OS = buffer.readString();
    }

    @Override
    public String toString() {
        return "HINFO " + CPU + " " + OS;
    }
}
