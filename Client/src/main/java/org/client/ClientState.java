package org.client;

import org.share.HeaderPacket;

public interface ClientState {
    boolean handleCommand(String input);
    void sendPacketToBytes(HeaderPacket packet);
    boolean commandExecute(String inputCommand);

}
