package org.client;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;
@Slf4j
public class ClientStarter {
    private static final int serverPort = 7777;
    public void start() {
        try {
            Socket socket = new Socket("localhost", serverPort);
            Client client = new Client(socket);
            //
            log.info("ClientState : {}", client.getClientState());
            //
            PacketSender packetSender = new PacketSender(socket, client);
            PacketReceiver packetReceiver = new PacketReceiver(socket, client);
            packetSender.start();
            packetReceiver.start();
            packetSender.join();
            packetReceiver.join();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
