package packetlist.beforeregister;

import lombok.Getter;
import org.share.HeaderPacket;
import org.share.PacketType;

@Getter
public class PacketForUserExit extends HeaderPacket {
    private final String name;

    public PacketForUserExit(String name){
        super(PacketType.Exit, 4 + name.getBytes().length);
        this.name = name;
    }
    public byte[] getBodyBytes() {
        byte[] nameBytes = name.getBytes();
        byte[] body = new byte[bodyLength];
        System.arraycopy(intToBytesArray(nameBytes.length),0,body,0,4);
        System.arraycopy(nameBytes,0,body,4,nameBytes.length);
        return body;
    }

    public static PacketForUserExit of(byte[] bodyBytes){
        int bodyLength = byteToInt(bodyBytes,0);
        String name = new String(bodyBytes,4,bodyLength);
        return new PacketForUserExit(name);
    }
}
