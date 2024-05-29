package org.client;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@Getter
@Setter
@Slf4j
public class PacketSender extends Thread {
    private final Socket socket;
    private Scanner scanner;
    private String userName;
    private Map<String, Command> registeredUserCommandMap = new HashMap<>();
    private Map<String, Command> unregisteredUserCommandMap = new HashMap<>();
    private Client client;

    public PacketSender(Socket socket, Client client) {
        this.socket = socket;
        this.client = client;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void run() {
        try {
            while (true) {
                String command = scanner.nextLine();
                log.info("user command : {}", command);
                boolean isKeepChatting = client.handleCommand(command);
                if (!isKeepChatting) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*
    private void sendPacketToBytes(HeaderPacket packet) {
        try {
            var outputStream = socket.getOutputStream();
            byte[] headerBytes = packet.getHeaderBytes();
            byte[] bodyBytes = packet.getBodyBytes();
            byte[] sendPacketBytes = new byte[headerBytes.length + bodyBytes.length];
            System.arraycopy(headerBytes, 0, sendPacketBytes, 0, headerBytes.length);
            System.arraycopy(bodyBytes, 0, sendPacketBytes, headerBytes.length, bodyBytes.length);
            outputStream.write(sendPacketBytes);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 쓰레드 처리해서 다른 작업도 병행시켜줘야 한다
    private void sendFilePacketToBytes(File file, String sender, String receiver) {
        PacketForFileInformationAndCheckReceiver packetForFileInformation = new PacketForFileInformationAndCheckReceiver(file.getName(), sender, receiver, file.length());
        try {
            sendPacketToBytes(packetForFileInformation);
            //통신 응답을 기다리게 해야 한다 >> 응답을 받은 다음에 이 코드를 실행시켜야 한다
            if (!PacketReceiver.isFileReceiverAbsentChecker()) {
                byte[] buffer = new byte[sendingByteSize]; // 10mb 정도면 한 번 보낼 때 무리 없이 보낼 수 있다고 함
                FileInputStream fileInputStream = new FileInputStream(file);
                int byteRead;
                int sendingCount = 0;
                int lastSendingCount = (int) Math.ceil(file.length() / sendingByteSize) - 1; // -1 해야 하나 >> 해줘야 한다 1부터 카운트 한 게 아니니깐 ceil메서드는 소수점 자리가 나오면 바로 반올림하는 메서드
                while ((byteRead = fileInputStream.read(buffer)) > 0) {
                    byte[] realSendingBuffer = new byte[byteRead];
                    System.arraycopy(buffer, 0, realSendingBuffer, 0, byteRead);
                    PacketForFileSending packetForFileSending = new PacketForFileSending(sendingCount, realSendingBuffer, lastSendingCount); //무슨 데이터를 보내줘야 하나....
                    sendPacketToBytes(packetForFileSending);
                    sendingCount++;
                }
                PacketReceiver.setFileReceiverAbsentChecker(false);
                fileInputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void unregisteredUserCommandMapInit() {
        unregisteredUserCommandMap.put("/register", args -> {
            userName = args[1];
            PacketForUserResister packetForUserResister = new PacketForUserResister(userName);
            sendPacketToBytes(packetForUserResister);
            return true;
        });
        unregisteredUserCommandMap.put("/exit", args -> {
            PacketForUserExit packetForUserExit = new PacketForUserExit(userName);
            sendPacketToBytes(packetForUserExit);
            return false;
        });
    }

    private void registeredUserCommandMapInit() {
        registeredUserCommandMap.put("/changeName", args -> {
            String newName = args[1];
            PacketForUserNameChange packetForUserNameChange = new PacketForUserNameChange(userName, newName);
            sendPacketToBytes(packetForUserNameChange);
            return true;
        });
        registeredUserCommandMap.put("/w", args -> {
            String userName = args[1];
            String directMessage = args[2];
            PacketForDM packetForDM = new PacketForDM(userName, userName, directMessage);
            sendPacketToBytes(packetForDM);
            return true;
        });
        registeredUserCommandMap.put("/f", args -> {
            String filePath = args[1];
            String receiver = args[2];
            File file = new File(filePath);
            if (file.exists()) {
                new Thread(() -> {
                    sendFilePacketToBytes(file, userName, receiver);
                }).start();
            } else {
                System.out.println("File does not exist");
            }
            return true;
        });
        registeredUserCommandMap.put("/exit", args -> {
            PacketForUserExit packetForUserExit = new PacketForUserExit(userName);
            sendPacketToBytes(packetForUserExit);
            return false;
        });
    }

    private void commandExecute(String inputCommand, Map<String, Command> commandMap) throws IOException {
        String[] args = inputCommand.split(" ");
        Command command = commandMap.get(args[0]);
        if (command != null) {
            command.execute(args);
        } else {
            System.out.println("Invalid commend, Please enter Correct Command");
        }
    }*/
}
