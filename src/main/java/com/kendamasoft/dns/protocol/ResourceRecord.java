package com.kendamasoft.dns.protocol;

import com.kendamasoft.dns.records.AbstractRecord;
import com.kendamasoft.dns.records.*;

/**
 * Resource Record
 */
public final class ResourceRecord {
    /**
     * Record name
     */
    String name;

    /**
     * @see RecordType
     */
    RecordType recordType;

    short recordTypeId;

    /**
     * @see QuestionEntry#questionClass
     */
    short recordClass;

    /**
     * ttl
     * unsigned 32-bit int
     */
    long ttl;

    short dataLength;

    AbstractRecord content;

    /**
     * @return record name
     */
    public String getName() {
        return name;
    }

    /**
     * @return record TTL
     */
    public long getTtl() {
        return ttl;
    }

    /**
     * @see RecordType
     * @return record type
     */
    public RecordType getRecordType() {
        return recordType;
    }

    /**
     * @see AAAARecord
     * @see ARecord
     * @see CAARecord
     * @see MXRecord
     * @see NSRecord
     * @see PTRRecord
     * @see SOARecord
     * @see SPFRecord
     * @see TXTRecord
     *
     * @return actual content of this record
     */
    public AbstractRecord getContent() {
        return content;
    }

    void readRecord(Buffer buffer) {
        if(recordType == null) {
            content = new UnknownRecord(null, recordTypeId);
        } else {
            try {
                content = recordType.getRecordClass().newInstance();
            } catch (Exception ex) {
                content = new UnknownRecord(recordType, recordTypeId);
            }
        }
        content.parseData(dataLength, buffer);
    }

    /**
     * @return string representation of record in form "domain.name. 6666 IN A 1.2.3.4"
     */
    @Override
    public String toString() {
        return name + " " + ttl + " IN " + content;
    }

}
