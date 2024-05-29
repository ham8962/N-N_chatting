package packetlist.beforeregister;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.share.HeaderPacket;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PacketForUserExitTest {
    private String name;
    private PacketForUserExit packetForUserExit;

    @BeforeEach
    void setup() {
        name = "testUser";
        packetForUserExit = new PacketForUserExit(name);
    }

    @Test
    void testGetBodyBytesAndOf() {
        //given
        byte[] packetBodyBytes = packetForUserExit.getBodyBytes();
        //when1
        int nameLength = HeaderPacket.byteToInt(packetBodyBytes, 0);
        String userName = new String(packetBodyBytes, 4, nameLength);
        //when2
        PacketForUserExit packetForUserExitTest = PacketForUserExit.of(packetBodyBytes);
        //then1
        assertEquals(name, userName);
        //then2
        assertEquals(packetForUserExit.getPacketType(), packetForUserExitTest.getPacketType());
        assertArrayEquals(packetForUserExit.getHeaderBytes(), packetForUserExitTest.getHeaderBytes());
        assertEquals(packetForUserExit.getName(), packetForUserExitTest.getName());
    }
}