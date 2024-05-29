package org.server;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.share.PacketType;

import java.net.Socket;

@Slf4j
public class User {
    private UserState userState;
    @Getter
    private ClientResisterManager clientResisterManager;

    public User(Socket socket, ClientResisterManager clientResisterManager) {
        this.clientResisterManager = clientResisterManager;
        this.userState = new UnregisteredUserState(this, socket);
    }

    public void setUserState(UserState newUserState) {
        this.userState = newUserState;
        log.info("UserState in ServerApplication is changed : {}", newUserState);
    }

    public boolean handlePacket(PacketType packetType, byte[] receivedBodyBytes) {
        return userState.handlePacket(packetType, receivedBodyBytes);
    }
}
