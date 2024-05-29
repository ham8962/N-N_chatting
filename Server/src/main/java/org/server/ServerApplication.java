package org.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerApplication {
    public static void main(String[] args) {
        ServerStarter serverStarter = new ServerStarter();
        serverStarter.start();
    }
}
