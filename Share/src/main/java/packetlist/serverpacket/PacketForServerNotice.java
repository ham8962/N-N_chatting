package packetlist.serverpacket;

import lombok.Getter;
import org.share.HeaderPacket;
import org.share.PacketType;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
@Getter
public class PacketForServerNotice extends HeaderPacket {
    private final String userName;
    public PacketForServerNotice(String userName) {
        super(PacketType.Server_Notice,4 + userName.getBytes().length);
        this.userName = userName;
    }

    @Override
    public byte[] getBodyBytes() {
        byte[] userNameBytes = userName.getBytes();
        byte[] bodyBytes = new byte[bodyLength];
        System.arraycopy(intToBytesArray(userNameBytes.length),0,bodyBytes,0,4);
        System.arraycopy(userNameBytes,0, bodyBytes, 4, userNameBytes.length);
        return bodyBytes;
    }

    public static PacketForServerNotice of(byte[] bodyBytes) {
        int userNameBytesLength = ByteBuffer.wrap(bodyBytes,0,4).getInt();
        String userName = new String(bodyBytes,4, userNameBytesLength, StandardCharsets.UTF_8);
        return new PacketForServerNotice(userName);
    }
}
