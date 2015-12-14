package com.kendamasoft.dns.records;

/**
 * Part of the Sender Policy Framework protocol<br>
 * <b>Obsolete</b>
 */
public class SPFRecord extends TXTRecord {
    @Override
    public String toString() {
        return "SPF " + data;
    }
}
