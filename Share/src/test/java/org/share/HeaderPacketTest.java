package org.share;

import org.junit.jupiter.api.Test;
import packetlist.beforeregister.PacketForUserResister;

import static org.junit.jupiter.api.Assertions.*;

class HeaderPacketTest {

    @Test
    void testIntToBytesArray() {
        int value = 123;
        byte[] testBytes = HeaderPacket.intToBytesArray(value);
        assertEquals(4, testBytes.length);
        assertEquals(123, HeaderPacket.byteToInt(testBytes, 0));
    }
    @Test
    void testGetHeaderBytes() {
        HeaderPacket headerPacket = new PacketForUserResister("testUser");
    }

    @Test
    void testByteToInt() {
    }

    @Test
    void testByteToPacketType() {
    }

    @Test
    void testByteToBodyLength() {
    }
}