package packetlist.serverpacket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PacketForUserRegistrationAcceptanceTest {
    private String testUserName = "testUser";
    private PacketForUserRegistrationAcceptance packetForUserRegistrationAcceptance;

    @BeforeEach
    void setUp() {
        packetForUserRegistrationAcceptance = new PacketForUserRegistrationAcceptance(testUserName);
    }

    @Test
    void getBodyBytesAndOf() {
        //given
        byte[] bodyBytes = packetForUserRegistrationAcceptance.getBodyBytes();
        ByteBuffer byteBuffer = ByteBuffer.wrap(bodyBytes);
        //when1
        int nameLength = byteBuffer.getInt();
        String name = new String(bodyBytes, 4, nameLength);
        //when2
        PacketForUserRegistrationAcceptance packetForUserRegistrationAcceptanceTest = PacketForUserRegistrationAcceptance.of(bodyBytes);
        //then1
        assertEquals(name, testUserName);
        //then2
        assertEquals(packetForUserRegistrationAcceptance.getPacketType(), packetForUserRegistrationAcceptanceTest.getPacketType());
        assertArrayEquals(packetForUserRegistrationAcceptance.getHeaderBytes(), packetForUserRegistrationAcceptanceTest.getHeaderBytes());
        assertEquals(packetForUserRegistrationAcceptance.getUserName(), packetForUserRegistrationAcceptanceTest.getUserName());
    }
}