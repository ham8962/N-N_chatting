package org.client;

import org.share.HeaderPacket;

public interface ServerPacketIdentifier {
    HeaderPacket serverPacketChecker(byte[] bodybytes);
}
