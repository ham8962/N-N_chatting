package packetlist.afterregister;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.share.HeaderPacket;

import static org.junit.jupiter.api.Assertions.*;

class PacketForUserMessageTest {
    private String testUserName;
    private String testMessage;
    private PacketForUserMessage packetForUserMessage;
    @BeforeEach
    void setup() {
        testUserName = "testUser";
        testMessage = "hello everyone";
        packetForUserMessage = new PacketForUserMessage(testMessage, testUserName);
    }

    @Test
    void testGetBodyBytesAndOf() {
        //given
        byte[] packetBodyBytes = packetForUserMessage.getBodyBytes();
        //when1
        int nameLength = HeaderPacket.byteToInt(packetBodyBytes, 0);
        String userName = new String(packetBodyBytes, 4, nameLength);
        int messageLength = HeaderPacket.byteToInt(packetBodyBytes, 4 + nameLength);
        String message = new String(packetBodyBytes, 8 + nameLength, messageLength);
        //when2
        PacketForUserMessage packetForUserMessageTest = PacketForUserMessage.of(packetBodyBytes);
        //then1
        assertEquals(testUserName, userName);
        assertEquals(testMessage, message);
        //then2
        assertEquals(packetForUserMessage.getPacketType(), packetForUserMessageTest.getPacketType());
        assertArrayEquals(packetForUserMessage.getHeaderBytes(), packetForUserMessageTest.getHeaderBytes());
        assertEquals(packetForUserMessage.getName(), packetForUserMessageTest.getName());
        assertEquals(packetForUserMessage.getUserMessage(), packetForUserMessageTest.getUserMessage());
    }
}