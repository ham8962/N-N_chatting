package packetlist.serverpacket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PacketForServerNoticeTest {
    private String testUserName = "testUser";
    private PacketForServerNotice packetForServerNotice;

    @BeforeEach
    void setUp() {
        packetForServerNotice = new PacketForServerNotice(testUserName);
    }

    @Test
    void getBodyBytesAndOf() {
        //given
        byte[] bodyBytes = packetForServerNotice.getBodyBytes();
        ByteBuffer byteBuffer = ByteBuffer.wrap(bodyBytes);
        //when1
        int nameLength = byteBuffer.getInt();
        String name = new String(bodyBytes, 4, nameLength);
        //when2
        PacketForServerNotice packetForServerNoticeTest = PacketForServerNotice.of(bodyBytes);
        //then1
        assertEquals(name, testUserName);
        //then2
        assertEquals(packetForServerNotice.getPacketType(), packetForServerNoticeTest.getPacketType());
        assertArrayEquals(packetForServerNotice.getHeaderBytes(), packetForServerNoticeTest.getHeaderBytes());
        assertEquals(packetForServerNotice.getUserName(), packetForServerNoticeTest.getUserName());
    }


}