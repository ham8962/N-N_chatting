package org.client;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;
@Slf4j
public class Client {
    @Getter
    private ClientState clientState;
    @Setter
    @Getter
    private String userName;
    @Getter
    private final Socket socket;

    public Client(Socket socket) {
        this.socket = socket;
        this.clientState = new UnregisteredState(this, socket);
    }
    public void setClientState(ClientState newClientState) {
        this.clientState = newClientState;
        log.info("ClientState in ClientApplication is Changed : {}", clientState);
    }
    public boolean handleCommand(String command) throws IOException {
        return clientState.handleCommand(command);
    }
}
