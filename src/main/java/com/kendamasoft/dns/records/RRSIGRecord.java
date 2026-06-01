package com.kendamasoft.dns.records;

import com.kendamasoft.dns.protocol.Buffer;

/**
 * DNSSEC signature record (RFC 4034)
 * @since 1.2.0
 */
public class RRSIGRecord extends AbstractRecord {

    int typeCovered;

    int algorithm;

    int labels;

    long originalTtl;

    long expiration;

    long inception;

    int keyTag;

    String signerName;

    byte[] signature;

    @Override
    public void parseData(short dataLength, Buffer buffer) {
        int start = buffer.position();
        typeCovered = buffer.readShort() & 0xFFFF;
        algorithm   = buffer.readByte()  & 0xff;
        labels      = buffer.readByte()  & 0xff;
        originalTtl = buffer.readUint32();
        expiration  = buffer.readUint32();
        inception   = buffer.readUint32();
        keyTag      = buffer.readShort() & 0xFFFF;
        signerName  = buffer.readString();
        signature   = RecordData.restOfRecord(buffer, dataLength, start);
    }

    public int getTypeCovered() {
        return typeCovered;
    }

    public int getAlgorithm() {
        return algorithm;
    }

    public int getLabels() {
        return labels;
    }

    public long getOriginalTtl() {
        return originalTtl;
    }

    public long getExpiration() {
        return expiration;
    }

    public long getInception() {
        return inception;
    }

    public int getKeyTag() {
        return keyTag;
    }

    public String getSignerName() {
        return signerName;
    }

    public byte[] getSignature() {
        return signature;
    }

    @Override
    public String toString() {
        return "RRSIG " + RecordData.typeName(typeCovered) + " " + algorithm + " " + labels + " "
                + originalTtl + " " + RecordData.sigTime(expiration) + " " + RecordData.sigTime(inception)
                + " " + keyTag + " " + signerName + " " + RecordData.toBase64(signature);
    }
}
