package packetlist.serverpacket;

import lombok.Getter;
import org.share.HeaderPacket;
import org.share.PacketType;

@Getter
public class PacketForReceiverAbsent extends HeaderPacket {

    public PacketForReceiverAbsent() {
        super(PacketType.ReceiverAbsent, 0);
    }

    @Override
    public byte[] getBodyBytes() {
        return new byte[0];
    }

    // static으로 자기 자신을 호출하는 of가 없어도 되는건ㅁ
    public static PacketForReceiverAbsent of() {
        return new PacketForReceiverAbsent();
    }
}