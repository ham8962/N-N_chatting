package packetlist.afterregister;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PacketForFileSendingTest {
    private int sendingCount;
    private int lastSendingCount;
    private byte[] buffer;
    private String fileName;
    private String sender;
    private String receiver;
    private PacketForFileSending packetForFileSending;

    @BeforeEach
    void setUp() {
        sendingCount = 1;
        lastSendingCount = 3;
        buffer = new byte[10];
        fileName = "testFile";
        sender = "testSender";
        receiver = "testReceiver";
        packetForFileSending = new PacketForFileSending(sendingCount, lastSendingCount, buffer, fileName, sender, receiver);
    }

    @Test
    void getBodyBytesAndOf() {
        //given
        byte[] packetBodyBytes = packetForFileSending.getBodyBytes();
        ByteBuffer buffer = ByteBuffer.wrap(packetBodyBytes);
        //when1
        int sendingCount = buffer.getInt();
        int lastSendingCount = buffer.getInt();
        int bufferLength = buffer.getInt();
        byte[] fileBuffer = new byte[bufferLength];
        buffer.get(fileBuffer);
        int fileNameLength = buffer.getInt();
        byte[] fileNameBytes = new byte[fileNameLength];
        buffer.get(fileNameBytes);
        String fileName = new String(fileNameBytes, StandardCharsets.UTF_8);
        int senderLength = buffer.getInt();
        byte[] senderBytes = new byte[senderLength];
        buffer.get(senderBytes);
        String sender = new String(senderBytes, StandardCharsets.UTF_8);
        int receiverLength = buffer.getInt();
        byte[] receiverBytes = new byte[receiverLength];
        buffer.get(receiverBytes);
        String receiver = new String(receiverBytes, StandardCharsets.UTF_8);
        //when2
        PacketForFileSending packetForFileSendingTest = PacketForFileSending.of(packetBodyBytes);
        //then1
        assertEquals(sendingCount, packetForFileSendingTest.getSendingCount());
        assertEquals(lastSendingCount, packetForFileSendingTest.getLastSendingCount());
        assertArrayEquals(fileBuffer, packetForFileSendingTest.getBuffer());
        assertEquals(fileName, packetForFileSendingTest.getFileName());
        assertEquals(sender, packetForFileSendingTest.getSender());
        assertEquals(receiver, packetForFileSendingTest.getReceiver());
        //then2
        assertEquals(packetForFileSending.getPacketType(), packetForFileSendingTest.getPacketType());
        assertArrayEquals(packetForFileSending.getHeaderBytes(), packetForFileSendingTest.getHeaderBytes());
        assertEquals(packetForFileSending.getBodyLength(), packetForFileSendingTest.getBodyLength());
        assertEquals(packetForFileSending.getSendingCount(), packetForFileSendingTest.getSendingCount());
        assertEquals(packetForFileSending.getLastSendingCount(), packetForFileSendingTest.getLastSendingCount());
        assertArrayEquals(packetForFileSending.getBuffer(), packetForFileSendingTest.getBuffer());
        assertEquals(packetForFileSending.getFileName(), packetForFileSendingTest.getFileName());
        assertEquals(packetForFileSending.getSender(), packetForFileSendingTest.getSender());
        assertEquals(packetForFileSending.getReceiver(), packetForFileSendingTest.getReceiver());
    }
}