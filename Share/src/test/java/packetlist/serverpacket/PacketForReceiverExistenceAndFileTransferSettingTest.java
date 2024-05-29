package packetlist.serverpacket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PacketForReceiverExistenceAndFileTransferSettingTest {
    private String testSender;
    private String testReceiver;
    private String testFilePath;
    private String testFileName;
    private PacketForReceiverExistenceAndFileTransferSetting packetForReceiverExistenceAndFileTransferSetting;

    @BeforeEach
    void setup() {
        testSender = "sender";
        testReceiver = "receiver";
        testFilePath = "filePath";
        testFileName = "fileName";
        packetForReceiverExistenceAndFileTransferSetting = new PacketForReceiverExistenceAndFileTransferSetting(testSender, testReceiver, testFilePath, testFileName);
    }

    @Test
    void testGetBodyBytesAndOf() {
        //given
        byte[] packetBodyBytes = packetForReceiverExistenceAndFileTransferSetting.getBodyBytes();
        ByteBuffer buffer = ByteBuffer.wrap(packetBodyBytes);
        //when1
        int senderLength = buffer.getInt();
        int receiverLength = buffer.getInt();
        int filePathLength = buffer.getInt();
        int fileNameLength = buffer.getInt();
        String sender = new String(packetBodyBytes, 16, senderLength, StandardCharsets.UTF_8);
        String receiver = new String(packetBodyBytes, 16 + senderLength, receiverLength, StandardCharsets.UTF_8);
        String filePath = new String(packetBodyBytes, 16 + senderLength + receiverLength, filePathLength, StandardCharsets.UTF_8);
        String fileName = new String(packetBodyBytes, 16 + senderLength + receiverLength + filePathLength, fileNameLength, StandardCharsets.UTF_8);
        //when2
        PacketForReceiverExistenceAndFileTransferSetting packetForReceiverExistenceAndFileTransferSettingTest = PacketForReceiverExistenceAndFileTransferSetting.of(packetBodyBytes);
        //then1
        assertEquals(sender,testSender);
        assertEquals(receiver, testReceiver);
        assertEquals(filePath, testFilePath);
        assertEquals(fileName, testFileName);
        //then2
        assertEquals(packetForReceiverExistenceAndFileTransferSetting.getPacketType(), packetForReceiverExistenceAndFileTransferSettingTest.getPacketType());
        assertArrayEquals(packetForReceiverExistenceAndFileTransferSetting.getHeaderBytes(), packetForReceiverExistenceAndFileTransferSettingTest.getHeaderBytes());
        assertEquals(packetForReceiverExistenceAndFileTransferSetting.getSender(), packetForReceiverExistenceAndFileTransferSettingTest.getSender());
        assertEquals(packetForReceiverExistenceAndFileTransferSetting.getReceiver(), packetForReceiverExistenceAndFileTransferSettingTest.getReceiver());
        assertEquals(packetForReceiverExistenceAndFileTransferSetting.getFilePath(), packetForReceiverExistenceAndFileTransferSettingTest.getFilePath());
        assertEquals(packetForReceiverExistenceAndFileTransferSetting.getFileName(), packetForReceiverExistenceAndFileTransferSettingTest.getFileName());
    }
}