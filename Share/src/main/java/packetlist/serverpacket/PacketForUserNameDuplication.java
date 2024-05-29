package packetlist.serverpacket;

import lombok.Getter;
import org.share.HeaderPacket;
import org.share.PacketType;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
@Getter
public class PacketForUserNameDuplication extends HeaderPacket {

    public PacketForUserNameDuplication(){
        super(PacketType.UserDuplication, 0);
    }

    @Override
    public byte[] getBodyBytes() {
        return new byte[0];
    }

    public static PacketForUserNameDuplication of() {
        return new PacketForUserNameDuplication();
    }
}
