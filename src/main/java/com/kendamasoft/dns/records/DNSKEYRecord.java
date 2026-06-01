package com.kendamasoft.dns.records;

import com.kendamasoft.dns.protocol.Buffer;

/**
 * DNS Key record (RFC 4034)
 * @since 1.2.0
 */
public class DNSKEYRecord extends AbstractRecord {

    int flags;

    int protocol;

    int algorithm;

    byte[] publicKey;

    @Override
    public void parseData(short dataLength, Buffer buffer) {
        flags     = buffer.readShort() & 0xFFFF;
        protocol  = buffer.readByte() & 0xff;
        algorithm = buffer.readByte() & 0xff;
        publicKey = RecordData.remaining(buffer, dataLength - 4);
    }

    public int getFlags() {
        return flags;
    }

    public int getProtocol() {
        return protocol;
    }

    public int getAlgorithm() {
        return algorithm;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    /**
     * @return record type name used in {@link #toString()}; overridden by identical-format subtypes.
     */
    protected String recordName() {
        return "DNSKEY";
    }

    @Override
    public String toString() {
        return recordName() + " " + flags + " " + protocol + " " + algorithm
                + " " + RecordData.toBase64(publicKey);
    }
}
