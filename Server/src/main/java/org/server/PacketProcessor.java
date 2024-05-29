package org.server;

import org.share.PacketType;

import java.nio.ByteBuffer;

public class PacketProcessor {
    private static final int HEADER_SIZE = 8;

    public boolean processPacket(byte[] packetData) {
        // 패킷의 최소 길이 검증
        if (packetData.length < HEADER_SIZE) {
            System.out.println("Invalid packet: Too short.");
            return false;
        }

        // 헤더 정보 추출
        ByteBuffer buffer = ByteBuffer.wrap(packetData);
        int packetTypeValue = buffer.getInt();
        int bodyLength = buffer.getInt();

        // 패킷 타입 검증
        if (!isValidPacketType(packetTypeValue)) {
            System.out.println("Invalid packet: Unknown packet type.");
            return false;
        }

        // 본문 길이 검증
        if (0 != packetData.length - HEADER_SIZE) {
            System.out.println("Invalid packet: Body length does not match.");
            return false;
        }

        return true;

        /* 본문 추출
        byte[] body = new byte[bodyLength];
        buffer.get(body);

        // 본문 처리
        processBody(body, packetType);
        */
    }

    private boolean isValidPacketType(int packetTypeValue) {
        PacketType packetType = PacketType.packetTypeFinderByValue(packetTypeValue);
        return packetType != null;
    }

    private void processBody(byte[] body, int packetType) {
        // 본문 처리 로직 구현
        // 예를 들어, 패킷 타입에 따라 다른 처리를 할 수 있습니다.
        switch (packetType) {
            case 1:
                // 사용자 메시지 처리
                String message = new String(body);
                System.out.println("Received message: " + message);
                break;
            case 2:
                // 파일 전송 처리
                // 파일 처리 로직...
                break;
            default:
                System.out.println("Unknown packet type.");
                break;
        }
    }

    public static void main(String[] args) {
        // 테스트를 위한 간단한 패킷 데이터
        byte[] packetData = ByteBuffer.allocate(HEADER_SIZE + 11)
                .putInt(1) // 패킷 타입: 1 (사용자 메시지)
                .putInt(11) // 본문 길이: "Hello World".getBytes().length
                .put("Hello World".getBytes())
                .array();

        new PacketProcessor().processPacket(packetData);
    }
}
