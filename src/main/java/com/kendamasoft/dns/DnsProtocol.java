package com.kendamasoft.dns;

import com.kendamasoft.dns.records.*;

import java.util.List;

/**
 * DNS Protocol data structures <br> <br>
 *
 * DNS Message in both way consists of <b>{@link Header}</b>, <b>{@link QuestionEntry}</b> <br>
 * and zero or any number of <b>{@link DnsProtocol.ResourceRecord}</b>
 * <br>
 * <br>
 * <a href="https://www.ietf.org/rfc/rfc1035.txt" target="_blank">https://www.ietf.org/rfc/rfc1035.txt</a> <br>
 * <a href="https://technet.microsoft.com/en-us/library/dd197470(v=ws.10).aspx" target="_blank">https://technet.microsoft.com/en-us/library/dd197470(v=ws.10).aspx</a> <br>
 * <a href="https://en.wikipedia.org/wiki/List_of_DNS_record_types" target="_blank">https://en.wikipedia.org/wiki/List_of_DNS_record_types</a> <br>
 */
public class DnsProtocol {

    static public final int MAX_MESSAGE_LENGTH = 512;

    static public final short QUESTION_CLASS_IN = 0x0001;

    /**
     * Resource Records types with <b>{@link RecordType#getId() codes}</b> and <b>{@link RecordType#getDescription() desciprtion}</b>.<br>
     * <br>
     * Full list:<br>
     * <a href="https://en.wikipedia.org/wiki/List_of_DNS_record_types" target="_blank">https://en.wikipedia.org/wiki/List_of_DNS_record_types</a><br>
     * <br>
     * Fully supported only commonly used record types: <br>
     * {@link AAAARecord}<br>
     * {@link ARecord}<br>
     * {@link MXRecord}<br>
     * {@link NSRecord}<br>
     * {@link TXTRecord}<br>
     * {@link SOARecord}<br>
     */
    public enum RecordType {
        /*********************************
         * Supported Record Types        *
         *********************************/

        /**
         * IPv4 host address
         */
        A    ((short)1,  ARecord.class,   "IPv4 host address"),
        /**
         * Name server
         */
        NS   ((short)2,  NSRecord.class,  "Name server"),
        /**
         * Start of [a zone of] authority record
         */
        SOA  ((short)6,  SOARecord.class, "Start of [a zone of] authority record"),
        /**
         * Pointer to a canonical name
         */
        PTR  ((short)12, PTRRecord.class, "Pointer to a canonical name"),
        /**
         * Mail exchange
         */
        MX   ((short)15, MXRecord.class,  "Mail exchange"),
        /**
         * Text record
         */
        TXT  ((short)16, TXTRecord.class, "Text record"),
        /**
         * IPv6 host address
         */
        AAAA ((short)28, AAAARecord.class,"IPv6 host address"),

        /*********************************
        * Not supported, but known types *
        **********************************/

        /**
         * Canonical name record
         */
        CNAME ((short)5,  UnknownRecord.class, "Canonical name record"),
        /**
         * Host information
         */
        HINFO ((short)13, UnknownRecord.class, "Host information"),
        /**
         * Responsible person
         */
        RP    ((short)17, UnknownRecord.class, "Responsible person"),
        /**
         * AFS database record
         */
        AFSDB ((short)18, UnknownRecord.class, "AFS database record"),
        /**
         * Signature
         */
        SIG   ((short)24, UnknownRecord.class, "Signature"),
        /**
         * Location record
         */
        LOC   ((short)29, UnknownRecord.class, "Location record"),
        /**
         * Service locator
         */
        SRV   ((short)33, UnknownRecord.class, "Service locator"),
        /**
         * Naming Authority Pointer
         */
        NAPTR ((short)35, UnknownRecord.class, "Naming Authority Pointer"),
        /**
         * Key eXchanger record
         */
        KX    ((short)36, UnknownRecord.class, "Key eXchanger record"),
        /**
         * Certificate record
         */
        CERT  ((short)37, UnknownRecord.class, "Certificate record"),
        /**
         * Delegation Name
         */
        DNAME ((short)39, UnknownRecord.class, "Delegation Name"),
        /**
         * Address Prefix List
         */
        APL   ((short)42, UnknownRecord.class, "Address Prefix List"),
        /**
         * Delegation signer
         */
        DS    ((short)43, UnknownRecord.class, "Delegation signer"),
        /**
         * SSH Public Key Fingerprint
         */
        SSHFP  ((short)44, UnknownRecord.class, "SSH Public Key Fingerprint"),
        /**
         * IPsec Key
         */
        IPSECKEY ((short)45, UnknownRecord.class, "IPsec Key"),
        /**
         * DNSSEC signature
         */
        RRSIG ((short)46, UnknownRecord.class, "DNSSEC signature"),
        /**
         * Next-Secure record
         */
        NSEC  ((short)47, UnknownRecord.class, "Next-Secure record"),
        /**
         * DNS Key record
         */
        DNSKEY((short)48, UnknownRecord.class, "DNS Key record"),
        /**
         * DHCP identifier
         */
        DHCID((short)49, UnknownRecord.class, "DHCP identifier"),
        /**
         * NSEC record version 3
         */
        NSEC3((short)50, UnknownRecord.class, "NSEC record version 3"),
        /**
         * NSEC3 parameters
         */
        NSEC3PARAM((short)51, UnknownRecord.class, "NSEC3 parameters"),
        /**
         * TLSA certificate association
         */
        TLSA((short)52, UnknownRecord.class, "TLSA certificate association"),
        /**
         * Host Identity Protocol
         */
        HIP((short)55, UnknownRecord.class, "Host Identity Protocol"),
        /**
         * Child DS
         */
        CDS((short)59, UnknownRecord.class, "Child DS"),
        /**
         * Child DNSKEY
         */
        CDNSKEY((short)60, UnknownRecord.class, "Child DNSKEY"),
        /**
         * Secret key record
         */
        TKEY((short)249, UnknownRecord.class, "Secret key record"),
        /**
         * Transaction Signature
         */
        TSIG((short)250, UnknownRecord.class, "Transaction Signature"),


        /**
         * Obsolete: Sender Policy Framework
         */
        SPF   ((short)99, SPFRecord.class, "Obsolete: Sender Policy Framework"),
        /**
         * Certification Authority Authorization
         */
        CAA   ((short)257, CAARecord.class, "Certification Authority Authorization"),

        /**
         * Type to request all known resource records for given domain name.
         */
        ANY   ((short)255, UnknownRecord.class, "All cached records");

        private final short id;

        private final Class<? extends Record> recordClass;

        private final String description;

        RecordType(short id, Class<? extends Record> recordClass, String description) {
            this.id = id;
            this.recordClass = recordClass;
            this.description = description;
        }

        /**
         * @return code of record type
         */
        public short getId() {
            return id;
        }

        /**
         * @return simple description of the type
         */
        public String getDescription() {
            return description;
        }

        static public RecordType getById(short id) {
            for(RecordType type : values()) {
                if(type.id == id) {
                    return type;
                }
            }
            return null;
        }
    }

    /**
     * DNS Message Header<br>
     * <code>
     * 2 octet transaction Id<br>
     * 2 octet flags<br>
     * 2 octet question resource count<br>
     * 2 octet answer resource count<br>
     * 2 octet authority resource count<br>
     * 2 octet additional resource count<br>
     * </code>
     * @see Header#flags
     */
    public static class Header {

        /**
         * 0 - request, 1 - response
         */
        static public final int FLAG_MESSAGE_TYPE           = (1 << 15);
        static public final int FLAG_AUTHORITATIVE_RESPONSE = (1 << 10);
        /**
         * 1 - data truncation, should switch connection from UDP to TCP
         */
        static public final int FLAG_TRUNCATION             = (1 << 9);
        static public final int FLAG_RECURSION_DESIRED      = (1 << 8);
        static public final int FLAG_RECURSION_AVAILABLE    = (1 << 7);

        short transactionId;

        /**
         * Flags bit layout (from most significant bit to less)<br>
         * <code>
         * first octet
         * [0]   - req [=0] / res [=1]
         * [1,4] - op code ([=0] for query)
         * [5]   - authoritative response [=1]
         * [6]   - truncation (if [=1] - exceeded limit in 512b)
         * [7]   - recursion desired [=1]
         * second octet
         * [0]   - recursion available
         * [1-3] - reserved
         * [4-7] - return code ([=0] success, [=0x3] error)
         * </code>
         */
        short flags;

        short questionResourceRecordCount;

        short answerResourceRecordsCount;

        short authorityResourceRecordsCount;

        short additionalResourceRecordsCount;

        public short getAnswerResourceRecordsCount() {
            return answerResourceRecordsCount;
        }

        public short getAuthorityResourceRecordsCount() {
            return authorityResourceRecordsCount;
        }

        public short getAdditionalResourceRecordsCount() {
            return additionalResourceRecordsCount;
        }

        /**
         * @see Header#FLAG_MESSAGE_TYPE
         * @see Header#FLAG_AUTHORITATIVE_RESPONSE
         * @see Header#FLAG_TRUNCATION
         * @see Header#FLAG_RECURSION_DESIRED
         * @see Header#FLAG_RECURSION_AVAILABLE
         * @param flag to check
         * @return is flag set
         */
        public boolean hasFlag(int flag) {
            return ((flags & 0xffff) & flag) > 0;
        }

        /**
         * @return operation code (zero for query)
         */
        public int opCode() {
            // 0b0111_0000_0000_0000;
            return flags & 0x7000;
        }

        /**
         * @return return code (zero - success, 0x3 - error)
         */
        public int returnCode() {
            // 0b0000_0111
            return flags & 0xf;
        }
    }

    /**
     * DNS resource question query. <br>
     * Not directly usable.
     */
    static class QuestionEntry {
        /**
         * Resource name (typically domain name we want to query)
         */
        String name;

        /**
         * Record type we want to know
         * @see RecordType
         */
        short type;

        /**
         * @see DnsProtocol#QUESTION_CLASS_IN
         */
        short questionClass;
    }

    /**
     * DNS protocol message model
     */
    public static class Message {
        Header header;
        QuestionEntry questionEntry;
        List<ResourceRecord> answerRecordList;
        List<ResourceRecord> authorityRecordList;
        List<ResourceRecord> additionalRecordList;

        public Header getHeader() {
            return header;
        }

        public List<ResourceRecord> getAnswerRecordList() {
            return answerRecordList;
        }

        public List<ResourceRecord> getAuthorityRecordList() {
            return authorityRecordList;
        }

        public List<ResourceRecord> getAdditionalRecordList() {
            return additionalRecordList;
        }
    }

    /**
     * Resource Record
     */
    public static class ResourceRecord {
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

        Record content;

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
        public Record getContent() {
            return content;
        }

        void readRecord(Buffer buffer) {
            if(recordType == null) {
                content = new UnknownRecord(null, recordTypeId);
            } else {
                try {
                    content = recordType.recordClass.newInstance();
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
}
