package com.kendamasoft.dns.records;

/**
 * HTTPS binding record (RFC 9460).
 * <p>
 * Shares the wire format of {@link SVCBRecord}; only the displayed type name differs.
 * @since 1.2.0
 */
public class HTTPSRecord extends SVCBRecord {

    @Override
    protected String recordName() {
        return "HTTPS";
    }
}
