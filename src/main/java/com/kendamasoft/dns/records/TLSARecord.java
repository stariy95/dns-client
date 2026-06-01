package com.kendamasoft.dns.records;

import com.kendamasoft.dns.protocol.Buffer;

/**
 * TLSA certificate association record (RFC 6698)
 * @since 1.2.0
 */
public class TLSARecord extends AbstractRecord {

    int certificateUsage;

    int selector;

    int matchingType;

    byte[] certificateAssociationData;

    @Override
    public void parseData(short dataLength, Buffer buffer) {
        certificateUsage           = buffer.readByte() & 0xff;
        selector                   = buffer.readByte() & 0xff;
        matchingType               = buffer.readByte() & 0xff;
        certificateAssociationData = RecordData.remaining(buffer, dataLength - 3);
    }

    public int getCertificateUsage() {
        return certificateUsage;
    }

    public int getSelector() {
        return selector;
    }

    public int getMatchingType() {
        return matchingType;
    }

    public byte[] getCertificateAssociationData() {
        return certificateAssociationData;
    }

    /**
     * @return record type name used in {@link #toString()}; overridden by identical-format subtypes.
     */
    protected String recordName() {
        return "TLSA";
    }

    @Override
    public String toString() {
        return recordName() + " " + certificateUsage + " " + selector + " " + matchingType
                + " " + RecordData.toHex(certificateAssociationData);
    }
}
