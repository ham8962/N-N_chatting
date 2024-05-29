package packetlist.serverpacket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PacketForUserExitFromServerTest {
    private String testUserName = "testUser";
    private int testChatCount = 3;
    private PacketForUserExitFromServer packetForUserExitFromServer;

    @BeforeEach
    void setUp() {
        packetForUserExitFromServer = new PacketForUserExitFromServer(testUserName, testChatCount);
    }

    @Test
    void getBodyBytesAndOf() {
        //given
        byte[] bodyBytes = packetForUserExitFromServer.getBodyBytes();
        ByteBuffer byteBuffer = ByteBuffer.wrap(bodyBytes);
        //when1
        int chatCount = byteBuffer.getInt();
        int nameLength = byteBuffer.getInt();
        String name = new String(bodyBytes, 8, nameLength);
        //when2
        PacketForUserExitFromServer packetForUserExitFromServerTest = PacketForUserExitFromServer.of(bodyBytes);
        //then1
        assertEquals(name, testUserName);
        assertEquals(chatCount, testChatCount);
        //then2
        assertEquals(packetForUserExitFromServer.getPacketType(), packetForUserExitFromServerTest.getPacketType());
        assertArrayEquals(packetForUserExitFromServer.getHeaderBytes(), packetForUserExitFromServerTest.getHeaderBytes());
        assertEquals(packetForUserExitFromServer.getName(), packetForUserExitFromServerTest.getName());
        assertEquals(packetForUserExitFromServer.getChatCount(), packetForUserExitFromServerTest.getChatCount());
    }

}