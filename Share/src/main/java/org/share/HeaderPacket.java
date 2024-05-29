package org.share;

import lombok.Getter;

import java.nio.ByteBuffer;

@Getter
public abstract class HeaderPacket {
    protected PacketType packetType;
    protected int bodyLength;

    public HeaderPacket(PacketType packetType, int bodyLength) {
        this.packetType = packetType;
        this.bodyLength = bodyLength;
    }

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
    }

}
