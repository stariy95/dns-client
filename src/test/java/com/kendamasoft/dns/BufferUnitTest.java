package com.kendamasoft.dns;

import org.junit.Test;

import static org.junit.Assert.*;

public class BufferUnitTest {

    Buffer buffer;

    public BufferUnitTest() {
    }

    @org.junit.Before
    public void setUp() throws Exception {
        buffer = new Buffer();
    }

    @Test
    public void testGetLength() throws Exception {

    }

    @Test
    public void testGetData() throws Exception {

    }

    @Test
    public void testWrite() throws Exception {
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
    public void testReadString() throws Exception {

    }

    @Test
    public void testWrite1() throws Exception {

    }

    @Test
    public void testReadByte() throws Exception {
        byte data[] = {1, 2, 3, 127, -128, 9, 0, 1};
        buffer = new Buffer(data);
        byte b = buffer.readByte();
        assertEquals(1, b);
    }

    @Test
    public void testWrite2() throws Exception {

    }

    @Test
    public void testReadShort() throws Exception {

    }
}