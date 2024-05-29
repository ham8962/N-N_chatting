package packetlist.serverpacket;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PacketForUserNameDuplicationTest {
    private PacketForUserNameDuplication packetForUserNameDuplication = new PacketForUserNameDuplication();

    @Test
    void of() {
        PacketForUserNameDuplication packetForUserNameDuplicationTest = PacketForUserNameDuplication.of();
        assertEquals(packetForUserNameDuplication.getPacketType(), packetForUserNameDuplicationTest.getPacketType());
        assertArrayEquals(packetForUserNameDuplication.getHeaderBytes(), packetForUserNameDuplicationTest.getHeaderBytes());
    }
}