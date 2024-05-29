package org.server;

import org.share.HeaderPacket;

import java.io.IOException;

public interface PacketHandler {
    boolean handle(HeaderPacket packet);
}
