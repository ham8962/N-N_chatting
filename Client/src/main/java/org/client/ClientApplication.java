package org.client;

import java.io.IOException;
import java.net.Socket;


public class ClientApplication {
    private static final int serverPort = 7777;
    public static void main(String[] args) {
        ClientStarter clientStarter = new ClientStarter();
        clientStarter.start();
    }

    /*
    public void start(){
        Socket socket;
        try {
            socket = new Socket("localhost",serverPort);
            PacketSender clientSender = new PacketSender(socket);
            PacketReceiver packetReceiver = new PacketReceiver(socket);
            clientSender.start();
            packetReceiver.start();
            clientSender.join();
            packetReceiver.join();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }*/

    /* 이름 검증
    public String userNameDuplicationChecker(Socket socket) throws IOException{
        OutputStream outputStream = socket.getOutputStream();
        Scanner scanner = new Scanner(System.in);
        while(true){
            System.out.println("Enter your name : ");
            String name = scanner.nextLine();
            PacketForUserResister packetForUserResister = new PacketForUserResister(name);
            byte[] headerByte = packetForUserResister.getHeaderBytes();
            byte[] bodyByte = packetForUserResister.getBodyBytes();
            byte[] packet = new byte[headerByte.length + bodyByte.length];
            System.arraycopy(headerByte,0,packet,0,headerByte.length);
            System.arraycopy(bodyByte,0,packet,headerByte.length,bodyByte.length);
            outputStream.write(packet);
        }
    }
     */

}
