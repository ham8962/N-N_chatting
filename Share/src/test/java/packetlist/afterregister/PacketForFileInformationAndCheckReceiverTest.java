package packetlist.afterregister;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PacketForFileInformationAndCheckReceiverTest {
    private String fileName;
    private String sender;
    private String receiver;
    private String filePath;
    private long fileSize;
    private PacketForFileInformationAndCheckReceiver packetForFileInformationAndCheckReceiver;

    @BeforeEach
    void setUp() {
        fileName = "testFile";
        sender = "testSender";
        receiver = "testReceiver";
        filePath = "testPath";
        fileSize = 1000;
        packetForFileInformationAndCheckReceiver = new PacketForFileInformationAndCheckReceiver(fileName, sender, receiver, filePath, fileSize);
    }

    @Test
    void getBodyBytesAndOf() {
        //given
        byte[] packetBodyBytes = packetForFileInformationAndCheckReceiver.getBodyBytes();
        ByteBuffer buffer = ByteBuffer.wrap(packetBodyBytes);
        //when1
        int fileNameLength = buffer.getInt();
        String fileName = new String(packetBodyBytes, buffer.position(), fileNameLength);
        buffer.position(buffer.position() + fileNameLength);
        int senderLength = buffer.getInt();
        String sender = new String(packetBodyBytes, buffer.position(), senderLength);
        buffer.position(buffer.position() + senderLength);
        int receiverLength = buffer.getInt();
        String receiver = new String(packetBodyBytes, buffer.position(), receiverLength);
        buffer.position(buffer.position() + receiverLength);
        int filePathLength = buffer.getInt();
        String filePath = new String(packetBodyBytes, buffer.position(), filePathLength);
        buffer.position(buffer.position() + filePathLength);
        long fileSize = buffer.getLong();
        //when2
        PacketForFileInformationAndCheckReceiver packetForFileInformationAndCheckReceiverTest = PacketForFileInformationAndCheckReceiver.of(packetBodyBytes);
        //then1
        assertEquals(fileName, fileName);
        assertEquals(sender, sender);
        assertEquals(receiver, receiver);
        assertEquals(filePath, filePath);
        assertEquals(fileSize, fileSize);
        //then2
        assertEquals(packetForFileInformationAndCheckReceiver.getPacketType(), packetForFileInformationAndCheckReceiverTest.getPacketType());
        assertArrayEquals(packetForFileInformationAndCheckReceiver.getHeaderBytes(), packetForFileInformationAndCheckReceiverTest.getHeaderBytes());
        assertEquals(packetForFileInformationAndCheckReceiver.getFileName(), packetForFileInformationAndCheckReceiverTest.getFileName());
        assertEquals(packetForFileInformationAndCheckReceiver.getSender(), packetForFileInformationAndCheckReceiverTest.getSender());
        assertEquals(packetForFileInformationAndCheckReceiver.getReceiver(), packetForFileInformationAndCheckReceiverTest.getReceiver());
        assertEquals(packetForFileInformationAndCheckReceiver.getFilePath(), packetForFileInformationAndCheckReceiverTest.getFilePath());
        assertEquals(packetForFileInformationAndCheckReceiver.getFileSize(), packetForFileInformationAndCheckReceiverTest.getFileSize());
    }
}