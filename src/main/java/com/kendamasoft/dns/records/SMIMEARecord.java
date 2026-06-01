package com.kendamasoft.dns.records;

/**
 * S/MIME certificate association record (RFC 8162).
 * <p>
 * Shares the wire format of {@link TLSARecord}; only the displayed type name differs.
 * @since 1.2.0
 */
public class SMIMEARecord extends TLSARecord {

    @Override
    protected String recordName() {
        return "SMIMEA";
    }
}
