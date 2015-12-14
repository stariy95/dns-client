package com.kendamasoft.dns.records;

import com.kendamasoft.dns.Buffer;
import com.kendamasoft.dns.Record;

/**
 * Certification Authority Authorization
 */
public class CAARecord extends Record {

    byte flag;
    String tag;
    String data;

    @Override
    public void parseData(short dataLength, Buffer buffer) {
        flag = buffer.readByte();
        StringBuilder sb = new StringBuilder();
        byte tagLength = buffer.readByte();
        dataLength -= 2;
        for(int i=0; i<dataLength; i++) {
            char c = (char)(buffer.readByte() & 0xff);
            sb.append(c);
        }
        tag = sb.substring(0, tagLength);
        data = sb.substring(tagLength);
    }

    @Override
    public String toString() {
        return "CCA " + tag + " " + data;
    }
}
