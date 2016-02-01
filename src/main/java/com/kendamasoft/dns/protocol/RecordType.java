package com.kendamasoft.dns.protocol;

import com.kendamasoft.dns.records.AbstractRecord;
import com.kendamasoft.dns.records.*;

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

    private final Class<? extends AbstractRecord> recordClass;

    private final String description;

    RecordType(short id, Class<? extends AbstractRecord> recordClass, String description) {
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

    public Class<? extends AbstractRecord> getRecordClass() {
        return recordClass;
    }
}
