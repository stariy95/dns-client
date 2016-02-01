package com.kendamasoft.dns.records;

import com.kendamasoft.dns.protocol.Buffer;

/**
 */
public abstract class AbstractRecord {

    public abstract void parseData(short dataLength, Buffer buffer);

}
