package com.kendamasoft.dns.records;

import com.kendamasoft.dns.Buffer;
import com.kendamasoft.dns.Record;

/**
 *
 */
public class HINFORecord extends Record {

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
