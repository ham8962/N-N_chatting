package packetlist.afterregister;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.share.HeaderPacket;
import org.share.PacketType;

import java.nio.charset.StandardCharsets;
@Getter
@Setter
@Slf4j
public class PacketForUserNameChange extends HeaderPacket {
    private final String name;
    private final String changedName;
    public PacketForUserNameChange(String name,String changedName){
        super(PacketType.ChangeUserName,8 + name.getBytes().length + changedName.getBytes().length);
        this.name = name;
        this.changedName = changedName;
    }
    public byte[] getBodyBytes() {
        byte[] nameBytes = name.getBytes();
        byte[] changedNameBytes = changedName.getBytes();
        byte[] body = new byte[bodyLength];
        System.arraycopy(intToBytesArray(nameBytes.length),0,body,0,4);
        System.arraycopy(nameBytes,0,body,4, nameBytes.length);
        System.arraycopy(intToBytesArray(changedNameBytes.length),0,body,4+nameBytes.length,4);
        System.arraycopy(changedNameBytes,0,body,8+nameBytes.length,changedNameBytes.length);
        return body;
    }

    public static PacketForUserNameChange of(byte[] bodyBytes){
        int nameByteLength = byteToInt(bodyBytes,0);
        String name = new String(bodyBytes,4,nameByteLength, StandardCharsets.UTF_8);
        int changedNameLength = byteToInt(bodyBytes,4+nameByteLength);
        String changedName = new String(bodyBytes,8+nameByteLength,changedNameLength,StandardCharsets.UTF_8);
        return new PacketForUserNameChange(name,changedName);
    }
}