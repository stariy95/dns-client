package com.kendamasoft.dns.records;

import com.kendamasoft.dns.Buffer;
import com.kendamasoft.dns.DnsProtocol;
import com.kendamasoft.dns.Record;

/**
 * Not Supported Resource Record <br>
 * If type is known it can be retrieved via {@link UnknownRecord#getType()} <br>
 * for unknown type use {@link UnknownRecord#getTypeId()} to get type code. <br>
 * Raw data can be retrieved via {@link UnknownRecord#getData()} method
 */
public class UnknownRecord extends Record {

    final DnsProtocol.RecordType type;
    final short recordTypeId;
    byte[] data;


    public UnknownRecord(DnsProtocol.RecordType type, short id) {
        this.type = type;
        recordTypeId = id;
    }

    /**
     * @return raw byte data of record
     */
    public byte[] getData() {
        return data;
    }

    /**
     * @return record type if known or null
     * @see UnknownRecord#getTypeId()
     */
    public DnsProtocol.RecordType getType() {
        return type;
    }

    /**
     * @return code of record type
     * @see UnknownRecord#getType()
     * @see com.kendamasoft.dns.DnsProtocol.RecordType
     */
    public short getTypeId() {
        return recordTypeId;
    }

    @Override
    public void parseData(short dataLength, Buffer buffer) {
        data = new byte[dataLength];
        buffer.fill(data, dataLength);
    }

    @Override
    public String toString() {
        if(type != null) {
            return type.name();
        } else {
            return "TYPE_" + recordTypeId;
        }
    }
}
