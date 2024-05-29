package org.server;

import org.share.PacketType;

public interface UserState {
    boolean handlePacket(PacketType packetType, byte[] receivedBodyFromClient);
}
