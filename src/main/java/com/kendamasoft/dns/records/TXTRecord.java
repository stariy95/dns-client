package com.kendamasoft.dns.records;

import com.kendamasoft.dns.Buffer;
import com.kendamasoft.dns.Record;

/**
 * Text record
 */
public class TXTRecord extends Record {
    String data;

    @Override
    public void parseData(short dataLength, Buffer buffer) {
        StringBuilder sb = new StringBuilder();
        /*byte length = */buffer.readByte(); // first byte is data length
        for(int i=0; i<dataLength-1; i++) {
            sb.append((char)(buffer.readByte() & 0xff));
        }
        data = sb.toString();
        if(data.contains(" ")) {
            data = "\"" + data + "\"";
        }
    }

    public String getText() {
        return data;
    }

    @Override
    public String toString() {
        return "TXT " + data;
    }
}
