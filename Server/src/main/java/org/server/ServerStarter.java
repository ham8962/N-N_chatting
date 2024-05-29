package org.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerStarter {
    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(7777);
            System.out.println("[Sever Start]");
            ClientResisterManager clientResisterManager = new ClientResisterManager();
            while (true) {
                // 클라이언트가 접속한 만큼 새로운 소켓이 생성되니 동시에 처리해줄 스레드 필요
                Socket socket = serverSocket.accept();
                System.out.println("[Client Connecting]");
                ServerReceiver serverReceiver = new ServerReceiver(socket, clientResisterManager);
                serverReceiver.setDaemon(true);
                serverReceiver.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
