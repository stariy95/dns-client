package com.kendamasoft.dns.protocol;

import com.kendamasoft.dns.records.AAAARecord;
import com.kendamasoft.dns.records.ARecord;
import com.kendamasoft.dns.records.HINFORecord;
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
    public void getByIdResolvesNewlyRegisteredTypes() {
        assertEquals(RecordType.DSYNC, RecordType.getById((short) 66));
        assertEquals(RecordType.HHIT, RecordType.getById((short) 67));
        assertEquals(RecordType.BRID, RecordType.getById((short) 68));
        assertEquals(RecordType.NID, RecordType.getById((short) 104));
        assertEquals(RecordType.L32, RecordType.getById((short) 105));
        assertEquals(RecordType.L64, RecordType.getById((short) 106));
        assertEquals(RecordType.LP, RecordType.getById((short) 107));
        assertEquals(RecordType.NXNAME, RecordType.getById((short) 128));
        assertEquals(RecordType.AVC, RecordType.getById((short) 258));
        assertEquals(RecordType.DOA, RecordType.getById((short) 259));
        assertEquals(RecordType.AMTRELAY, RecordType.getById((short) 260));
        assertEquals(RecordType.RESINFO, RecordType.getById((short) 261));
        assertEquals(RecordType.WALLET, RecordType.getById((short) 262));
        assertEquals(RecordType.CLA, RecordType.getById((short) 263));
        assertEquals(RecordType.IPN, RecordType.getById((short) 264));
    }

    @Test
    public void hinfoIsWiredToItsParserClass() {
        assertEquals(13, RecordType.HINFO.getId());
        assertEquals(HINFORecord.class, RecordType.HINFO.getRecordClass());
    }

    @Test
    public void newlyRegisteredTypesExposeDescriptions() {
        assertEquals(264, RecordType.IPN.getId());
        assertEquals("Resolver Information as Key/Value Pairs", RecordType.RESINFO.getDescription());
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
