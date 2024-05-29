package org.client;

import org.share.HeaderPacket;

public interface ServerPacketHandler {
    boolean serverPacketHandle(HeaderPacket packet);
}
