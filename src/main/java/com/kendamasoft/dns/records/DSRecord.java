package com.kendamasoft.dns.records;

import com.kendamasoft.dns.protocol.Buffer;

/**
 * Delegation Signer record (RFC 4034)
 * @since 1.2.0
 */
public class DSRecord extends AbstractRecord {

    int keyTag;

    int algorithm;

    int digestType;

    byte[] digest;

    @Override
    public void parseData(short dataLength, Buffer buffer) {
        keyTag     = buffer.readShort() & 0xFFFF;
        algorithm  = buffer.readByte() & 0xff;
        digestType = buffer.readByte() & 0xff;
        digest     = RecordData.remaining(buffer, dataLength - 4);
    }

    public int getKeyTag() {
        return keyTag;
    }

    public int getAlgorithm() {
        return algorithm;
    }

    public int getDigestType() {
        return digestType;
    }

    public byte[] getDigest() {
        return digest;
    }

    /**
     * @return record type name used in {@link #toString()}; overridden by identical-format subtypes.
     */
    protected String recordName() {
        return "DS";
    }

    @Override
    public String toString() {
        return recordName() + " " + keyTag + " " + algorithm + " " + digestType
                + " " + RecordData.toHex(digest);
    }
}
