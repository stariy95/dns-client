package com.kendamasoft.dns.records;

import com.kendamasoft.dns.protocol.Buffer;

/**
 * MAC address (EUI-48) record (RFC 7043)
 * @since 1.2.0
 */
public class EUI48Record extends AbstractRecord {

    final byte[] address = new byte[6];

    @Override
    public void parseData(short dataLength, Buffer buffer) {
        buffer.fill(address, address.length);
    }

    public byte[] getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "EUI48 " + RecordData.toHex(address, '-');
    }
}
