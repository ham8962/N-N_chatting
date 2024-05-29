package packetlist.beforeregister;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PacketForUserResisterTest {
    private String testUserName;
    private PacketForUserResister2 packetForUserResister2;

    @BeforeEach
    void setup() {
        testUserName = "testUser";
        packetForUserResister2 = new PacketForUserResister2(testUserName);
    }

    /*
    @Test
    void testGetBodyBytes() {
        //given
        byte[] packetBodyBytes = packetForUserResister.getBodyBytes();
        //when
        int nameLength = HeaderPacket.byteToInt(packetBodyBytes, 0);
        String userName = new String(packetBodyBytes, 4, nameLength);
        //then
        assertEquals(testUserName, userName);
    }

    @Test
    void testOf() {
        //given
        byte[] bodyBytes = packetForUserResister.getBodyBytes();
        //when
        PacketForUserResister packetForUserResisterTest = PacketForUserResister.of(bodyBytes);
        //then
        assertEquals(packetForUserResister.getPacketType(), packetForUserResisterTest.getPacketType());
        assertEquals(packetForUserResister.getHeaderBytes(), packetForUserResisterTest.getHeaderBytes());
        assertEquals(packetForUserResister.getUserName(), packetForUserResisterTest.getUserName());
    }
    */
    @Test
    void testGetBodyBytesAndOf() {
        //given
        String json = packetForUserResister2.toJson();
        //when1
        PacketForUserResister2 packetForUserResister2Test = PacketForUserResister2.of(json);
        //when2

        //then1
        assertEquals(packetForUserResister2.getPacketType(), packetForUserResister2Test.getPacketType());
        assertEquals(packetForUserResister2.getUserName(), packetForUserResister2Test.getUserName());
        //then2

    }
}