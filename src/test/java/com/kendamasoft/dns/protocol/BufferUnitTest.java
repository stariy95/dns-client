package com.kendamasoft.dns.protocol;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BufferUnitTest {

    Buffer buffer;

    @Before
    public void setUp() {
        buffer = new Buffer();
    }

    @Test
    public void testWrite() {
        byte b1 = 1;
        byte b2 = 0;
        byte b3 = -127;
        byte b4 = -128;
        buffer.write(b1);
        buffer.write(b2);
        buffer.write(b3);
        buffer.write(b4);

        int length = buffer.getLength();
        assertEquals(4, length);
        byte[] data = buffer.getData();
        assertNotNull(data);

        assertEquals(b1, data[0]);
        assertEquals(b2, data[1]);
        assertEquals(b3, data[2]);
        assertEquals(b4, data[3]);
    }

    @Test
    public void testReadByte() {
        byte[] data = {1, 2, 3, 127, -128, 9, 0, 1};
        buffer = new Buffer(data);
        byte b = buffer.readByte();
        assertEquals(1, b);
    }

}