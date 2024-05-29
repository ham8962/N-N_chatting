package packetlist.afterregister;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.share.HeaderPacket;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PacketForUserNameChangeTest {
    private String name;
    private String changedName;
    private PacketForUserNameChange packetForUserNameChange;

    @BeforeEach
    void setUp() {
        name = "name";
        changedName = "changedName";
        packetForUserNameChange = new PacketForUserNameChange(name, changedName);
    }

    @Test
    void testGetBodyBytesAndOf() {
        //given
        byte[] packetBodyBytes = packetForUserNameChange.getBodyBytes();
        //when1
        int nameLength = HeaderPacket.byteToInt(packetBodyBytes, 0);
        String name = new String(packetBodyBytes, 4, nameLength);
        int changedNameLength = HeaderPacket.byteToInt(packetBodyBytes, 4 + nameLength);
        String changedName = new String(packetBodyBytes, 8 + nameLength, changedNameLength);
        //when2
        PacketForUserNameChange packetForUserNameChangeTest = PacketForUserNameChange.of(packetBodyBytes);
        //then1
        assertEquals(name, name);
        assertEquals(changedName, changedName);
        //then2
        assertEquals(packetForUserNameChange.getPacketType(), packetForUserNameChangeTest.getPacketType());
        assertArrayEquals(packetForUserNameChange.getHeaderBytes(), packetForUserNameChangeTest.getHeaderBytes());
        assertEquals(packetForUserNameChange.getName(), packetForUserNameChangeTest.getName());
        assertEquals(packetForUserNameChange.getChangedName(), packetForUserNameChangeTest.getChangedName());
    }
}