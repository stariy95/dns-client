package com.kendamasoft.dns.records;

import com.kendamasoft.dns.protocol.Buffer;

import java.util.List;

/**
 * NSEC record version 3 (RFC 5155)
 * @since 1.2.0
 */
public class NSEC3Record extends AbstractRecord {

    int hashAlgorithm;

    int flags;

    int iterations;

    byte[] salt;

    byte[] nextHashedOwner;

    List<Integer> types;

    @Override
    public void parseData(short dataLength, Buffer buffer) {
        int start = buffer.position();
        hashAlgorithm   = buffer.readByte()  & 0xff;
        flags           = buffer.readByte()  & 0xff;
        iterations      = buffer.readShort() & 0xFFFF;
        salt            = RecordData.remaining(buffer, buffer.readByte() & 0xff);
        nextHashedOwner = RecordData.remaining(buffer, buffer.readByte() & 0xff);
        types           = RecordData.bitmapTypes(RecordData.restOfRecord(buffer, dataLength, start));
    }

    public int getHashAlgorithm() {
        return hashAlgorithm;
    }

    public int getFlags() {
        return flags;
    }

    public int getIterations() {
        return iterations;
    }

    public byte[] getSalt() {
        return salt;
    }

    public byte[] getNextHashedOwner() {
        return nextHashedOwner;
    }

    /**
     * @return ascending Resource Record type ids present in the type bit map
     */
    public List<Integer> getTypes() {
        return types;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("NSEC3 ")
                .append(hashAlgorithm).append(' ')
                .append(flags).append(' ')
                .append(iterations).append(' ')
                .append(salt.length == 0 ? "-" : RecordData.toHex(salt)).append(' ')
                .append(RecordData.toBase32Hex(nextHashedOwner));
        for (int id : types) {
            sb.append(' ').append(RecordData.typeName(id));
        }
        return sb.toString();
    }
}
