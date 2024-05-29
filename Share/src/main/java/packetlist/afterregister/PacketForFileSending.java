package packetlist.afterregister;

import lombok.Getter;
import org.share.HeaderPacket;
import org.share.PacketType;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Getter
public class PacketForFileSending extends HeaderPacket {
    private final int sendingCount;
    private byte[] buffer;
    private final int lastSendingCount;
    private final String fileName;
    private final String sender;
    private final String receiver;

    public PacketForFileSending(int sendingCount, int lastSendingCount, byte[] buffer, String fileName, String sender, String receiver) {
        super(PacketType.SendFile, 24 + buffer.length + fileName.getBytes(StandardCharsets.UTF_8).length + sender.getBytes(StandardCharsets.UTF_8).length + receiver.getBytes(StandardCharsets.UTF_8).length);
        this.sendingCount = sendingCount;
        this.lastSendingCount = lastSendingCount;
        this.buffer = buffer;
        this.fileName = fileName;
        this.sender = sender;
        this.receiver = receiver;
    }

    @Override
    public byte[] getBodyBytes() {
        byte[] fileNameBytes = fileName.getBytes(StandardCharsets.UTF_8);
        byte[] senderBytes = sender.getBytes(StandardCharsets.UTF_8);
        byte[] receiverBytes = receiver.getBytes(StandardCharsets.UTF_8);
        ByteBuffer bodyBuffer = ByteBuffer.allocate(super.getBodyLength());
        bodyBuffer.putInt(sendingCount);
        bodyBuffer.putInt(lastSendingCount);
        bodyBuffer.putInt(buffer.length);
        bodyBuffer.put(buffer);
        bodyBuffer.putInt(fileNameBytes.length);
        bodyBuffer.put(fileNameBytes);
        bodyBuffer.putInt(senderBytes.length);
        bodyBuffer.put(senderBytes);
        bodyBuffer.putInt(receiverBytes.length);
        bodyBuffer.put(receiverBytes);
        return bodyBuffer.array();
    }

    public static PacketForFileSending of(byte[] bodyBytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bodyBytes);
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
        return new PacketForFileSending(sendingCount, lastSendingCount, fileBuffer, fileName, sender, receiver);
    }
}
