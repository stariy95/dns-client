package com.kendamasoft.dns.records;

import com.kendamasoft.dns.protocol.Buffer;

import java.util.List;

/**
 * Next-Secure record (RFC 4034)
 * @since 1.2.0
 */
public class NSECRecord extends AbstractRecord {

    String nextDomainName;

    List<Integer> types;

    @Override
    public void parseData(short dataLength, Buffer buffer) {
        int start = buffer.position();
        nextDomainName = buffer.readString();
        types = RecordData.bitmapTypes(RecordData.restOfRecord(buffer, dataLength, start));
    }

    public String getNextDomainName() {
        return nextDomainName;
    }

    /**
     * @return ascending Resource Record type ids present in the type bit map
     */
    public List<Integer> getTypes() {
        return types;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("NSEC ").append(nextDomainName);
        for (int id : types) {
            sb.append(' ').append(RecordData.typeName(id));
        }
        return sb.toString();
    }
}
