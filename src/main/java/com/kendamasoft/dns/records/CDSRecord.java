package com.kendamasoft.dns.records;

/**
 * Child DS record (RFC 7344).
 * <p>
 * Shares the wire format of {@link DSRecord}; only the displayed type name differs.
 * @since 1.2.0
 */
public class CDSRecord extends DSRecord {

    @Override
    protected String recordName() {
        return "CDS";
    }
}
