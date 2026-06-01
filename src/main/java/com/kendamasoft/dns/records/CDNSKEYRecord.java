package com.kendamasoft.dns.records;

/**
 * Child DNSKEY record (RFC 7344).
 * <p>
 * Shares the wire format of {@link DNSKEYRecord}; only the displayed type name differs.
 * @since 1.2.0
 */
public class CDNSKEYRecord extends DNSKEYRecord {

    @Override
    protected String recordName() {
        return "CDNSKEY";
    }
}
