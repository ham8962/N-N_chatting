package packetlist.afterregister;

import lombok.Getter;
import lombok.Setter;
import org.share.HeaderPacket;
import org.share.PacketType;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Getter
@Setter
public class PacketForFileInformationAndCheckReceiver extends HeaderPacket {
    private final String fileName;
    private final String sender;
    private final String receiver;
    private final long fileSize;
    private final String filePath;

    public PacketForFileInformationAndCheckReceiver(String fileName, String sender, String receiver, String filePath, long fileSize){
        super(PacketType.FileInformation,16 + fileName.getBytes().length + sender.getBytes().length + receiver.getBytes().length + filePath.getBytes().length + 8); //20 은 12 + 파일 사이즈에 대한 수(8byte)
        this.fileName = fileName;
        this.sender = sender;
        this.receiver = receiver;
        this.filePath = filePath;
        this.fileSize = fileSize;
    }

    @Override
    public byte[] getBodyBytes() {
        byte[] fileNameBytes = fileName.getBytes();
        byte[] senderBytes = sender.getBytes();
        byte[] receiverBytes = receiver.getBytes();
        byte[] filePathBytes = filePath.getBytes();
        ByteBuffer buffer = ByteBuffer.allocate(16 + fileNameBytes.length + senderBytes.length + receiverBytes.length + filePathBytes.length + 8); // 파일 이름, 발신자, 수신자, 파일 경로 길이와 파일 사이즈(8byte)를 포함한 크기
        buffer.putInt(fileNameBytes.length);
        buffer.put(fileNameBytes);
        buffer.putInt(senderBytes.length);
        buffer.put(senderBytes);
        buffer.putInt(receiverBytes.length);
        buffer.put(receiverBytes);
        buffer.putInt(filePathBytes.length);
        buffer.put(filePathBytes);
        buffer.putLong(fileSize); // 파일 사이즈 추가
        return buffer.array();
    }

    public static PacketForFileInformationAndCheckReceiver of(byte[] bodyBytes){
        ByteBuffer buffer = ByteBuffer.wrap(bodyBytes);

        int fileNameBytesLength = buffer.getInt();
        String fileName = new String(bodyBytes, buffer.position(), fileNameBytesLength, StandardCharsets.UTF_8);
        buffer.position(buffer.position() + fileNameBytesLength); // fileName을 읽은 후 위치 업데이트

        int senderBytesLength = buffer.getInt();
        String sender = new String(bodyBytes, buffer.position(), senderBytesLength, StandardCharsets.UTF_8);
        buffer.position(buffer.position() + senderBytesLength); // sender를 읽은 후 위치 업데이트

        int receiverBytesLength = buffer.getInt();
        String receiver = new String(bodyBytes, buffer.position(), receiverBytesLength, StandardCharsets.UTF_8);
        buffer.position(buffer.position() + receiverBytesLength); // receiver를 읽은 후 위치 업데이트

        int filePathBytesLength = buffer.getInt();
        String filePath = new String(bodyBytes, buffer.position(), filePathBytesLength, StandardCharsets.UTF_8);
        buffer.position(buffer.position() + filePathBytesLength);

        long fileSize = buffer.getLong(); // long으로 파일 사이즈 읽기
        return new PacketForFileInformationAndCheckReceiver(fileName, sender, receiver, filePath, fileSize);
    }

}
