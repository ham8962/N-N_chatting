package packetlist.beforeregister;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.share.HeaderPacket2;
import org.share.PacketType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
public class PacketForUserResister2 extends HeaderPacket2 {
    @JsonProperty("userName")
    private final String userName;
    private static final Logger logger = LoggerFactory.getLogger(PacketForUserResister.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public PacketForUserResister2(@JsonProperty("userName") String userName){
        super(PacketType.Resister);
        this.userName = userName;
        logger.debug("PacketForUserResister is created. / packetType: {}, userName: {}", super.getPacketType(), userName);
    }

    public String toJson() {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static PacketForUserResister2 of(String json) {
        try {
            return objectMapper.readValue(json, PacketForUserResister2.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
