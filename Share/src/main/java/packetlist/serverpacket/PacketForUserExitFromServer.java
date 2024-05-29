package packetlist.serverpacket;

import lombok.Getter;
import org.share.HeaderPacket;
import org.share.PacketType;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Getter
public class PacketForUserExitFromServer extends HeaderPacket {
    private final String name;
    private final int chatCount;

    public PacketForUserExitFromServer(String name, int chatCount){
        super(PacketType.Exit, 8 + name.getBytes().length);
        this.name = name;
        this.chatCount = chatCount;
    }
    public byte[] getBodyBytes() {
        byte[] nameBytes = name.getBytes();
        ByteBuffer byteBuffer = ByteBuffer.allocate(8 + nameBytes.length);
        byteBuffer.putInt(chatCount);
        byteBuffer.putInt(nameBytes.length);
        byteBuffer.put(nameBytes);
        return byteBuffer.array();
    }

    public static PacketForUserExitFromServer of(byte[] bodyBytes){
        ByteBuffer byteBuffer = ByteBuffer.wrap(bodyBytes);
        int chatCount = byteBuffer.getInt();
        int nameLength = byteBuffer.getInt();
        String name = new String(bodyBytes,8, nameLength, StandardCharsets.UTF_8);
        return new PacketForUserExitFromServer(name, chatCount);
    }
}
