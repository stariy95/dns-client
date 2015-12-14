package com.kendamasoft.dns;

import com.kendamasoft.dns.Buffer;

/**
 */
public abstract class Record {

    public abstract void parseData(short dataLength, Buffer buffer);

}
