package packetlist.afterregister;

import lombok.Getter;
import org.share.HeaderPacket;
import org.share.PacketType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import packetlist.beforeregister.PacketForUserResister;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Getter
public class PacketForUserMessage extends HeaderPacket {
    private final String userMessage;
    private final String name;
    private static final Logger logger = LoggerFactory.getLogger(PacketForUserMessage.class);

    public PacketForUserMessage(String userMessage, String name){
        super(PacketType.SendMessageToALL,8 + name.getBytes().length + userMessage.getBytes().length);
        this.userMessage = userMessage;
        this.name = name;
        logger.debug("PacketForUserMessage is created / Message : {} , userName : {}", userMessage, name);
    }

    //이름 길이,메세지 길이 필요
    public byte[] getBodyBytes(){
        byte[] nameBytes = name.getBytes();
        byte[] messageBytes = userMessage.getBytes();
        byte[] bodyBytes = new byte[bodyLength];
        System.arraycopy(intToBytesArray(nameBytes.length),0,bodyBytes,0,4);
        System.arraycopy(nameBytes,0, bodyBytes, 4, nameBytes.length);
        System.arraycopy(intToBytesArray(messageBytes.length),0, bodyBytes,4 + nameBytes.length, 4);
        System.arraycopy(messageBytes,0, bodyBytes,8+nameBytes.length ,messageBytes.length);
        logger.debug("PacketForUserMessage's getBodyBytes method is called, bodyByte is created / userName: {}, bodyBytesLength: {}", name, bodyBytes.length);
        return bodyBytes;
    }

    public static PacketForUserMessage of (byte[] bodyBytes){
        int nameBytesLength = ByteBuffer.wrap(bodyBytes,0,4).getInt();
        String userName = new String(bodyBytes,4, nameBytesLength,StandardCharsets.UTF_8);
        int userMessageLength = ByteBuffer.wrap(bodyBytes, 4 + nameBytesLength,4).getInt();
        String userMessage = new String(bodyBytes,8+nameBytesLength, userMessageLength,StandardCharsets.UTF_8);
        return new PacketForUserMessage(userMessage,userName);
    }

}
