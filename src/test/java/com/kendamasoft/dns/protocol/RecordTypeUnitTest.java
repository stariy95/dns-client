package com.kendamasoft.dns.protocol;

import com.kendamasoft.dns.records.AAAARecord;
import com.kendamasoft.dns.records.ARecord;
import com.kendamasoft.dns.records.MXRecord;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the public {@link RecordType} enum lookup and accessors.
 */
public class RecordTypeUnitTest {

    @Test
    public void getByIdReturnsMatchingType() {
        assertEquals(RecordType.A, RecordType.getById((short) 1));
        assertEquals(RecordType.NS, RecordType.getById((short) 2));
        assertEquals(RecordType.CNAME, RecordType.getById((short) 5));
        assertEquals(RecordType.MX, RecordType.getById((short) 15));
        assertEquals(RecordType.AAAA, RecordType.getById((short) 28));
        assertEquals(RecordType.CAA, RecordType.getById((short) 257));
    }

    @Test
    public void getByIdReturnsNullForUnknownId() {
        assertNull(RecordType.getById((short) 9999));
    }

    @Test
    public void accessorsExposeIdClassAndDescription() {
        assertEquals(1, RecordType.A.getId());
        assertEquals(ARecord.class, RecordType.A.getRecordClass());
        assertEquals("IPv4 host address", RecordType.A.getDescription());

        assertEquals(28, RecordType.AAAA.getId());
        assertEquals(AAAARecord.class, RecordType.AAAA.getRecordClass());

        assertEquals(15, RecordType.MX.getId());
        assertEquals(MXRecord.class, RecordType.MX.getRecordClass());
    }
}
