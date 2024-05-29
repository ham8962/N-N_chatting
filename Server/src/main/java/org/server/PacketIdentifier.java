package org.server;

import org.share.HeaderPacket;

public interface PacketIdentifier {
    HeaderPacket packetChecker(byte[] bodyBytes);
}
