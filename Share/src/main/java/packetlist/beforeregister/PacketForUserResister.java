package packetlist.beforeregister;

import lombok.Getter;
import org.share.HeaderPacket;
import org.share.PacketType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
public class PacketForUserResister extends HeaderPacket {
    private final String userName;
    private static final Logger logger = LoggerFactory.getLogger(PacketForUserResister.class);

    public PacketForUserResister(String userName){
        super(PacketType.Resister, userName.getBytes().length + 4);
        this.userName = userName;
        logger.debug("PacketForUserResister is created. / packetType: {}, userName: {}", super.getPacketType(), userName);
        logger.debug("PacketForUserResister is created. / headerBytesLength: {}, headerBytes : {}", super.getHeaderBytes().length, super.getHeaderBytes());
    }

    public byte[] getBodyBytes(){
        byte[] nameBytes = userName.getBytes() ;
        byte[] bodyBytes = new byte[bodyLength];
        System.arraycopy(intToBytesArray(nameBytes.length),0,bodyBytes,0,4);
        System.arraycopy(nameBytes,0,bodyBytes,4, nameBytes.length);
        logger.debug("PacketForUserResister's getBodyBytes method is called, bodyByte is created /  bodyBytesLength: {}, bodyBytes{}", bodyBytes.length, bodyBytes);
        return bodyBytes;
    }

    public static PacketForUserResister of(byte[] bodyBytes){
        logger.debug("PacketForUserResister's of method is called, bodyBytes : {}", bodyBytes);
        int nameLength = byteToInt(bodyBytes,0);
        String userName = new String(bodyBytes,4,nameLength); //4패킷타입, 4 바디길이, 4이름 길이
        logger.debug("PacketForUserResister's of method is called, userName : {}", userName);
        return new PacketForUserResister(userName);
    }
}
