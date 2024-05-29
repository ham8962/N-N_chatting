package org.share;

import java.util.Arrays;

public enum PacketType {
    Resister(1),
    SendMessageToALL(2),
    Exit(3),
    Server_Notice(4),
    ClientExit(5),
    ChangeUserName(6),
    DirectMessage(7),
    FileInformation(8),
    ReceiverExistence(9),
    ReceiverAbsent(10),
    SendFile(11),
    UserDuplication(12),
    UserRegistrationAcceptance(13);

    private int value;

    PacketType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static PacketType packetTypeFinderByValue(int value) {
        return Arrays.stream(PacketType.values())
                .filter(packetType -> packetType.getValue() == value)
                .findFirst()
                .orElse(null);
    }
}
