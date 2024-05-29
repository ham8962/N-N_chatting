package packetlist.afterregister;

import lombok.Getter;
import org.share.HeaderPacket;
import org.share.PacketType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Getter
public class PacketForDM extends HeaderPacket {
    private final String directMessage;
    private final String senderName;
    private final String receiverName;
    private static final Logger logger = LoggerFactory.getLogger(PacketForDM.class);
    public PacketForDM(String senderName, String userName, String directMessage){
        super(PacketType.DirectMessage, 12 + senderName.getBytes().length + userName.getBytes().length + directMessage.getBytes().length);
        this.senderName = senderName;
        this.receiverName = userName;
        this.directMessage = directMessage;
    }

    @Override
    public byte[] getBodyBytes() {
        byte[] senderNameBytes = senderName.getBytes();
        byte[] userNameBytes = receiverName.getBytes();
        byte[] messageBytes = directMessage.getBytes();
        byte[] bodyBytes = new byte[bodyLength];
        System.arraycopy(intToBytesArray(senderNameBytes.length),0,bodyBytes,0,4);
        System.arraycopy(senderNameBytes,0,bodyBytes,4, senderNameBytes.length);
        System.arraycopy(intToBytesArray(userNameBytes.length),0,bodyBytes,4 + senderNameBytes.length,4);
        System.arraycopy(userNameBytes,0, bodyBytes, 8 + senderNameBytes.length, userNameBytes.length);
        System.arraycopy(intToBytesArray(messageBytes.length),0, bodyBytes,8 + senderNameBytes.length + userNameBytes.length, 4);
        System.arraycopy(messageBytes,0, bodyBytes,12 + senderNameBytes.length + userNameBytes.length ,messageBytes.length);
        return bodyBytes;
    }

    public static PacketForDM of(byte[] bodyBytes){
        int senderNameBytesLength = ByteBuffer.wrap(bodyBytes,0,4).getInt();
        String senderName = new String(bodyBytes,4, senderNameBytesLength, StandardCharsets.UTF_8);
        int userNameBytesLength = ByteBuffer.wrap(bodyBytes,4 + senderNameBytesLength,4).getInt();
        String userName = new String(bodyBytes,8 + senderNameBytesLength, userNameBytesLength, StandardCharsets.UTF_8);
        int directMessageBytesLength = ByteBuffer.wrap(bodyBytes, 8 + senderNameBytesLength + userNameBytesLength,4).getInt();
        String directMessaage = new String(bodyBytes,12 + senderNameBytesLength + userNameBytesLength, directMessageBytesLength,StandardCharsets.UTF_8);
        return new PacketForDM(senderName, userName, directMessaage);
    }
}
