package com.kendamasoft.dns;

//import android.util.Log;

import java.util.ArrayList;

/**
 * Byte buffer utility functions.<br>
 * <b><i>Only for internal use.</i></b>
 */
public class Buffer {

    private byte[] data = new byte[DnsProtocol.MAX_MESSAGE_LENGTH];

    private int length = 0;

    private int mark = 0;

    Buffer() {
        data = new byte[DnsProtocol.MAX_MESSAGE_LENGTH];
    }

    Buffer(byte[] data) {
        this.data = data;
        this.length = data.length;
    }

    int getLength() {
        return length;
    }

    byte[] getData() {
        byte[] result = new byte[length];
        System.arraycopy(data, 0, result, 0, length);
        return result;
    }

    private void checkRange() {
        if(mark >= length) {
            throw new ArrayIndexOutOfBoundsException("Buffer overflow. Tried to read after it's end. Buffer length = " + length);
        }
    }

    void write(DnsProtocol.Header header) {
        write(header.transactionId);
        write(header.flags);
        write(header.questionResourceRecordCount);
        write(header.answerResourceRecordCount);
        write(header.authorityResourceRecordsCount);
        write(header.additionalResourceRecordsCount);
    }

    DnsProtocol.Header readHeader() {
        DnsProtocol.Header header = new DnsProtocol.Header();
        header.transactionId = readShort();
        header.flags = readShort();
        header.questionResourceRecordCount = readShort();
        header.answerResourceRecordCount = readShort();
        header.authorityResourceRecordsCount = readShort();
        header.additionalResourceRecordsCount = readShort();
        return header;
    }

    void write(DnsProtocol.QuestionEntry questionEntry) {
        write(questionEntry.name);
        write(questionEntry.type);
        write(questionEntry.questionClass);
    }

    DnsProtocol.QuestionEntry readQuestionEntry() {
        DnsProtocol.QuestionEntry questionEntry = new DnsProtocol.QuestionEntry();
        questionEntry.name = readString();
        questionEntry.type = readShort();
        questionEntry.questionClass = readShort();
        return questionEntry;
    }

    DnsProtocol.ResourceRecord readResourceRecord() {
        DnsProtocol.ResourceRecord record = new DnsProtocol.ResourceRecord();
        record.name = readString();
        record.recordTypeId = readShort();
        record.recordType = DnsProtocol.RecordType.getById(record.recordTypeId);
        record.recordClass = readShort();
        record.ttl = readUint32();
        record.dataLength = readShort();
        record.readRecord(this);
        return record;
    }

    void write(DnsProtocol.Message message) {
        write(message.header);
        write(message.questionEntry);
    }

    DnsProtocol.Message readMessage() {
        DnsProtocol.Message message = new DnsProtocol.Message();
        message.header = readHeader();
        message.questionEntry = readQuestionEntry();
        if(message.header.hasFlag(DnsProtocol.Header.FLAG_TRUNCATION)) {
            return message;
        }
        if(message.header.answerResourceRecordCount > 0) {
            message.answerRecordList = new ArrayList<DnsProtocol.ResourceRecord>(message.header.answerResourceRecordCount);
            for (int i=0; i<message.header.answerResourceRecordCount; i++) {
                message.answerRecordList.add(readResourceRecord());
            }
        }
        if(message.header.authorityResourceRecordsCount > 0) {
            message.authorityRecordList = new ArrayList<DnsProtocol.ResourceRecord>(message.header.authorityResourceRecordsCount);
            for (int i=0; i<message.header.authorityResourceRecordsCount; i++) {
                message.authorityRecordList.add(readResourceRecord());
            }
        }
        if(message.header.additionalResourceRecordsCount > 0) {
            message.additionalRecordList = new ArrayList<DnsProtocol.ResourceRecord>(message.header.additionalResourceRecordsCount);
            for (int i=0; i<message.header.additionalResourceRecordsCount; i++) {
                message.additionalRecordList.add(readResourceRecord());
            }
        }
        return message;
    }

    /**
     * @param s string to write into buffer
     */
    void write(String s) {
        String[] chunks = s.split("\\.");
        for(String chunk : chunks) {
            char[] chars = chunk.toCharArray();
            write((byte)chars.length);
            for(char c : chars) {
                // @todo check the char value to be in a valid ASCII range
                write((byte)c);
            }
        }
        write((byte)0);
    }

    /*
    *       4.1.4. Message compression
        In order to reduce the size of messages, the domain system utilizes a
        compression scheme which eliminates the repetition of domain names in a
        message.  In this scheme, an entire domain name or a list of labels at
        the end of a domain name is replaced with a pointer to a prior occurance
        of the same name.

        The pointer takes the form of a two octet sequence:

            +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
            | 1  1|                OFFSET                   |
            +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+

        The first two bits are ones.  This allows a pointer to be distinguished
        from a label, since the label must begin with two zero bits because
        labels are restricted to 63 octets or less.  (The 10 and 01 combinations
        are reserved for future use.)  The OFFSET field specifies an offset from
        the start of the message (i.e., the first octet of the ID field in the
        domain header).  A zero offset specifies the first byte of the ID field,
        etc.

        The compression scheme allows a domain name in a message to be
        represented as either:
           - a sequence of labels ending in a zero octet
           - a pointer
           - a sequence of labels ending with a pointer

        Pointers can only be used for occurances of a domain name where the
        format is not class specific.  If this were not the case, a name server
        or resolver would be required to know the format of all RRs it handled.
        As yet, there are no such cases, but they may occur in future RDATA
        formats.

        If a domain name is contained in a part of the message subject to a
        length field (such as the RDATA section of an RR), and compression is
        used, the length of the compressed name is used in the length
        calculation, rather than the length of the expanded name.
    **/
    public String readString() {
        StringBuilder sb = new StringBuilder();
        while(true) {
            int length = readByte() & 0xff;
            if(length > 0x7f) {
                int next = readByte() & 0xff;
                sb.append(readStringCompressed(((length ^ 192) << 8) | next));
                break;
            }
            if(length == 0) {
                break;
            }
            for (int i = 0; i < length; i++) {
                sb.append((char)readByte());
            }
            sb.append('.');
        }
        return sb.toString();
    }

    private String readStringCompressed(int offset) {
        int oldMark = mark;
        mark = offset;
        String result = readString();
        mark = oldMark;
        return result;
    }

    void write(byte b) {
        data[length++] = b;
    }

    public byte readByte() {
        checkRange();
        return data[mark++];
    }

    void write(short s) {
        data[length++] = (byte)(s >> 8);
        data[length++] = (byte) s;
    }

    public short readShort() {
        short b1 = (short)(readByte() & 0xff);
        short b2 = (short)(readByte() & 0xff);
        return (short)(b1 << 8 | b2);
    }

    public long readUint32() {
        return ((readShort() & 0xFFFF) << 16) | (readShort() & 0xFFFF);
    }

    public void fill(byte[] data, int length) {
        System.arraycopy(this.data, mark, data, 0, length);
        advance(length);
    }

    void advance(int advance) {
        mark += advance;
    }

    void dump() {
        StringBuilder sb = new StringBuilder(">>>>>>>> DATA BUFFER DUMP <<<<<<<<\n");
        for(int i = 0; i < data.length; i += 4) {
            for(int j=i;j<i+4;j++) {
                if(j >= data.length) {
                    break;
                }
                // ASCII printable [48,122]
                if(data[j] >= 48 && data[j] <= 122) {
                    sb.append((char)data[j]);
                } else {
                    sb.append(data[j] & 0xff);
                }
                sb.append(" ");
            }
            sb.append("\n");
        }
        sb.append(">>>>>>> DATA BUFFER DUMP END <<<<<<");
        System.out.println(sb.toString());
        //Log.d("DNS_DATA_BUFFER", sb.toString());
    }
}

