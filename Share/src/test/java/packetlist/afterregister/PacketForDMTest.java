package packetlist.afterregister;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PacketForDMTest {
    private String senderName;
    private String receiverName;
    private String directMessage;
    private PacketForDM packetForDM;

    @BeforeEach
    void setUp() {
        senderName = "Tom";
        receiverName = "Jerry";
        directMessage = "HELLO JERRY THIS IS TOM";
        packetForDM = new PacketForDM(senderName, receiverName, directMessage);
    }

    @Test
    void testGetBodyBytesAndOf() {
        //given
        byte[] packetBodyBytes = packetForDM.getBodyBytes();

        //when2
        PacketForDM packetForDMTest = PacketForDM.of(packetBodyBytes);

        //then2
        assertEquals(packetForDM.getPacketType(), packetForDMTest.getPacketType());
        assertArrayEquals(packetForDM.getHeaderBytes(), packetForDMTest.getHeaderBytes());
        assertEquals(packetForDM.getSenderName(), packetForDMTest.getSenderName());
        assertEquals(packetForDM.getReceiverName(), packetForDMTest.getReceiverName());
        assertEquals(packetForDM.getDirectMessage(), packetForDMTest.getDirectMessage());
    }
}