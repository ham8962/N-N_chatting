package packetlist.serverpacket;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PacketForReceiverAbsentTest {
    private PacketForReceiverAbsent packetForReceiverAbsent = new PacketForReceiverAbsent();

    @Test
    void of() {
        PacketForReceiverAbsent packetForReceiverAbsentTest = PacketForReceiverAbsent.of();
        assertEquals(packetForReceiverAbsent.getPacketType(), packetForReceiverAbsentTest.getPacketType());
        assertArrayEquals(packetForReceiverAbsent.getHeaderBytes(), packetForReceiverAbsentTest.getHeaderBytes());
    }
}