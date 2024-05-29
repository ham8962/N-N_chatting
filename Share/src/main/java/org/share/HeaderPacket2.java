package org.share;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

@Getter
public abstract class HeaderPacket2 {
    @JsonProperty("packetType")
    protected PacketType packetType;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public HeaderPacket2(PacketType packetType) {
        this.packetType = packetType;
    }
    public String toJson() {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static HeaderPacket2 fromJson(String json) {
        try {
            return objectMapper.readValue(json, HeaderPacket2.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
    /*
    public static byte[] intToBytesArray(int value) {
        byte[] bytes = ByteBuffer.allocate(4).putInt(value).array();
        return bytes;
    }

    public byte[] getHeaderBytes() {
        byte[] headerBytes = new byte[8];
        System.arraycopy(intToBytesArray(packetType.getValue()), 0, headerBytes, 0, 4);
        System.arraycopy(intToBytesArray(bodyLength), 0, headerBytes, 4, 4);
        return headerBytes;
    }

    abstract public byte[] getBodyBytes();

    public static int byteToInt(byte[] intBytesArray, int start) {
        return ByteBuffer.wrap(intBytesArray, start, 4).getInt();
    }

    public static PacketType byteToPacketType(byte[] header) {
        int type = byteToInt(header, 0);
        return PacketType.packetTypeFinderByValue(type);
    }

    public static int byteToBodyLength(byte[] header) {
        int bodyLength = byteToInt(header, 4);
        return bodyLength;
    }*/
}
