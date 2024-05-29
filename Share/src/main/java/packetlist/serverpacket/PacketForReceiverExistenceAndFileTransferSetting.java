package packetlist.serverpacket;

import lombok.Getter;
import org.share.HeaderPacket;
import org.share.PacketType;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
@Getter
public class PacketForReceiverExistenceAndFileTransferSetting extends HeaderPacket {
    //전송자, 수신자, 파일 이름
    private final String sender;
    private final String receiver;
    private final String filePath;
    private final String fileName;
    public PacketForReceiverExistenceAndFileTransferSetting(String sender, String receiver, String filePath, String fileName) {
        super(PacketType.ReceiverExistence, 16 + sender.getBytes().length + receiver.getBytes().length + filePath.getBytes().length + fileName.getBytes().length);
        this.sender = sender;
        this.receiver = receiver;
        this.filePath = filePath;
        this.fileName = fileName;
    }

    @Override
    public byte[] getBodyBytes() {
        byte[] senderBytes = sender.getBytes();
        byte[] receiverBytes = receiver.getBytes();
        byte[] filePathBytes = filePath.getBytes();
        byte[] fileNameBytes = fileName.getBytes();
        byte[] bodyBytes = new byte[bodyLength];
        System.arraycopy(intToBytesArray(senderBytes.length), 0, bodyBytes, 0, 4);
        System.arraycopy(intToBytesArray(receiverBytes.length),0, bodyBytes,4, 4);
        System.arraycopy(intToBytesArray(filePathBytes.length), 0, bodyBytes, 8, 4);
        System.arraycopy(intToBytesArray(fileNameBytes.length),0, bodyBytes,12, 4);
        System.arraycopy(senderBytes, 0, bodyBytes,16, senderBytes.length);
        System.arraycopy(receiverBytes, 0, bodyBytes,16 + senderBytes.length, receiverBytes.length);
        System.arraycopy(filePathBytes, 0, bodyBytes, 16 + senderBytes.length + receiverBytes.length, filePathBytes.length);
        System.arraycopy(fileNameBytes, 0, bodyBytes, 16 + senderBytes.length + receiverBytes.length + filePathBytes.length, fileNameBytes.length);
        return bodyBytes;
    }

    public static PacketForReceiverExistenceAndFileTransferSetting of(byte[] bodyBytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bodyBytes);
        int senderBytesLength = buffer.getInt();
        int receiverBytesLength = buffer.getInt();
        int filePathBytesLength = buffer.getInt();
        int fileNameBytesLength = buffer.getInt();
        String sender = new String(bodyBytes, 16, senderBytesLength, StandardCharsets.UTF_8);
        String receiver = new String(bodyBytes, 16 + senderBytesLength, receiverBytesLength, StandardCharsets.UTF_8);
        String filePath = new String(bodyBytes, 16 + senderBytesLength + receiverBytesLength, filePathBytesLength, StandardCharsets.UTF_8);
        String fileName = new String(bodyBytes, 16 + senderBytesLength + receiverBytesLength + filePathBytesLength, fileNameBytesLength, StandardCharsets.UTF_8);
        return new PacketForReceiverExistenceAndFileTransferSetting(sender, receiver, filePath, fileName);
    }
}
