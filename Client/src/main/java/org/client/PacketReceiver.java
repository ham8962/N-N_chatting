package org.client;

import lombok.extern.slf4j.Slf4j;
import org.share.HeaderPacket;
import org.share.PacketType;
import packetlist.afterregister.*;
import packetlist.serverpacket.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
@Slf4j
public class PacketReceiver extends Thread {
    private final Socket socket;
    private final Scanner scanner;
    private Map<PacketType, ServerPacketIdentifier> serverPacketMap = new HashMap<>();
    private Map<PacketType, ServerPacketHandler> serverPakcetHandlerMap = new HashMap<>();
    private Client client;
    private ExecutorService executorService;

    public PacketReceiver(Socket socket, Client client) {
        this.socket = socket;
        this.client = client;
        this.scanner = new Scanner(System.in);
        this.executorService = Executors.newCachedThreadPool();
        initServerPacketMap();
        initServerPacketHandlerMap();
    }

    //서버에서도 프로토콜 규정대로 보내줄테니 그걸 분석해서 타입과 바디 안의 메세지를 뽑아내야 한다(아직 byte 다루는 부분 미숙)
    //밑의 메서드 수정 필요
    @Override
    public void run() {
        try {
            InputStream inputStream = socket.getInputStream();
            while (true) {
                // 가장 기본적인 메세지 전송만을 위해 만든 메서드, 원래 헤더 패킷 종류별로 어떻게 대응해야 할지 케이스를 나눠야 한다
                byte[] receivedHeaderByteFromServer = new byte[8];
                inputStream.read(receivedHeaderByteFromServer);
                log.debug("PacketReceiver's run method is working./ receivedHeaderLength: {}, PacketType : {}, receivedHeaderBytes: {}", receivedHeaderByteFromServer.length, HeaderPacket.byteToPacketType(receivedHeaderByteFromServer), receivedHeaderByteFromServer);
                PacketType packetTypeOfReceivedByte = HeaderPacket.byteToPacketType(receivedHeaderByteFromServer);
                int bodyLength = HeaderPacket.byteToBodyLength(receivedHeaderByteFromServer);
                byte[] receivedBodyByteFromServer = new byte[bodyLength];
                int lengthOfReceivedBodyByteFromServer = inputStream.read(receivedBodyByteFromServer);
                log.debug("PacketReceiver's run method is working./ receivedBodyBytesLength : {}, receivedBodyBytes : {}", receivedBodyByteFromServer.length, receivedBodyByteFromServer);
                if (packetTypeOfReceivedByte != null && lengthOfReceivedBodyByteFromServer > 0) {
                    ServerPacketIdentifier serverPacketIdentifier = serverPacketMap.getOrDefault(packetTypeOfReceivedByte, null);
                    log.debug("PacketReceiver's run method is working./ {}", serverPacketIdentifier);
                    ServerPacketHandler serverPacketHandler = serverPakcetHandlerMap.getOrDefault(packetTypeOfReceivedByte, null);
                    log.debug("PacketReceiver's run method is working./ {}",serverPacketHandler);
                    if (serverPacketIdentifier != null && serverPacketHandler != null) {
                        HeaderPacket packet = serverPacketIdentifier.serverPacketChecker(receivedBodyByteFromServer);
                        log.debug("PacketReceiver's run method is working./ {}", packet.getPacketType());
                        if (!serverPacketHandler.serverPacketHandle(packet)) {
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }

    private void initServerPacketMap() {
        serverPacketMap.put(PacketType.UserDuplication, bodyBytes -> PacketForUserNameDuplication.of()); // 둘 다 (가입 실패시, 이름 변경 실패시)
        serverPacketMap.put(PacketType.UserRegistrationAcceptance, bodyBytes -> PacketForUserRegistrationAcceptance.of(bodyBytes)); // 둘 다
        serverPacketMap.put(PacketType.SendMessageToALL, bodyBytes -> PacketForUserMessage.of(bodyBytes)); // 가입한 유저만
        serverPacketMap.put(PacketType.ChangeUserName, bodyBytes -> PacketForUserNameChange.of(bodyBytes)); // 가입한 유저만
        serverPacketMap.put(PacketType.Server_Notice, bodyBytes -> PacketForServerNotice.of(bodyBytes)); // 보류(유저 이름 변경 성공시 이게 날라오긴 함)
        serverPacketMap.put(PacketType.ReceiverExistence, bodyBytes -> PacketForReceiverExistenceAndFileTransferSetting.of(bodyBytes)); // 가입한 유저만
        serverPacketMap.put(PacketType.ReceiverAbsent, bodyBytes -> PacketForReceiverAbsent.of());
        serverPacketMap.put(PacketType.DirectMessage, bodyBytes -> PacketForDM.of(bodyBytes)); //가입한 유저만
        serverPacketMap.put(PacketType.FileInformation, bodyBytes -> PacketForFileInformationAndCheckReceiver.of(bodyBytes)); // 가입한 유저만
        serverPacketMap.put(PacketType.SendFile, bodyBytes -> PacketForFileSending.of(bodyBytes)); // 가입한 유저만
        serverPacketMap.put(PacketType.Exit, bodyBytes -> PacketForUserExitFromServer.of(bodyBytes)); // 둘 다
    }

    private void initServerPacketHandlerMap() {
        System.out.println("Please enter a path to save the file");
        String filePath = scanner.nextLine();
        FileReceiveManager fileReceiveManager = new FileReceiveManager(filePath);

        serverPakcetHandlerMap.put(PacketType.UserRegistrationAcceptance, packet -> {
            PacketForUserRegistrationAcceptance packetForUserRegistrationAcceptance = (PacketForUserRegistrationAcceptance) packet;
            System.out.println(packetForUserRegistrationAcceptance.getUserName() + " " + "is connected to Server");
            client.setClientState(new RegisteredState(client, socket));
            return true;
        });

        serverPakcetHandlerMap.put(PacketType.SendMessageToALL, packet -> {
            PacketForUserMessage packetForUserMessage = (PacketForUserMessage) packet;
            System.out.println(packetForUserMessage.getUserMessage());
            return true;
        });

        serverPakcetHandlerMap.put(PacketType.UserDuplication, packet -> {
            PacketForUserNameDuplication packetForUserNameDuplication = (PacketForUserNameDuplication) packet;
            System.out.println("Duplicated name, Please enter another Name");
            return true;
        });


        serverPakcetHandlerMap.put(PacketType.Server_Notice, packet -> {
            PacketForServerNotice packetForServerNotice = (PacketForServerNotice) packet;
            System.out.println(packetForServerNotice.getUserName());
            client.setUserName(packetForServerNotice.getUserName());
            return true;
        });

        serverPakcetHandlerMap.put(PacketType.DirectMessage, packet -> {
            PacketForDM packetForDM = (PacketForDM) packet;
            System.out.println(packetForDM.getDirectMessage());
            return true;
        });

        serverPakcetHandlerMap.put(PacketType.ReceiverExistence, packet -> {
            PacketForReceiverExistenceAndFileTransferSetting packetForReceiverExistenceAndFileTransferSetting = (PacketForReceiverExistenceAndFileTransferSetting) packet;
            log.debug("sender : {} / receiver : {} / filePath : {} / fileName : {}" ,
                    packetForReceiverExistenceAndFileTransferSetting.getSender(),
                    packetForReceiverExistenceAndFileTransferSetting.getReceiver(),
                    packetForReceiverExistenceAndFileTransferSetting.getFilePath(),
                    packetForReceiverExistenceAndFileTransferSetting.getFileName());
            System.out.println("File Receiver is exist");
            // 파일 전송 작업을 스레드 풀에 제출
            executorService.submit(() -> {
                try {
                    FileTransferManager fileTransferManager = new FileTransferManager(socket);
                    fileTransferManager.set(
                            packetForReceiverExistenceAndFileTransferSetting.getSender(),
                            packetForReceiverExistenceAndFileTransferSetting.getReceiver(),
                            packetForReceiverExistenceAndFileTransferSetting.getFilePath(),
                            packetForReceiverExistenceAndFileTransferSetting.getFileName()
                    );
                    fileTransferManager.sendFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            return true;
        });

        serverPakcetHandlerMap.put(PacketType.ReceiverAbsent, packet -> {
            System.out.println("File Receiver is absent");
            return true;
        });

        serverPakcetHandlerMap.put(PacketType.SendFile, packet -> {
            PacketForFileSending packetForFileSending = (PacketForFileSending) packet;
            log.debug("{}", packetForFileSending);
            fileReceiveManager.receiveFilePacket(packetForFileSending);
            return true;
        });

        serverPakcetHandlerMap.put(PacketType.Exit, packet -> {
            PacketForUserExitFromServer packetForUserExitFromServer = (PacketForUserExitFromServer) packet;
            String exitUserName = packetForUserExitFromServer.getName();
            log.debug("exitUserName from PacketForUserExitFromServer : {}", exitUserName);
            int exitUserCount = packetForUserExitFromServer.getChatCount();
            log.debug("exitUserCount from PacketForUserExitFromServer : {}", exitUserCount);
            System.out.println("User : " + "[" + exitUserName + "]" + " is disconnected from Server /" + "Current User Count : " + exitUserCount);
            return false;
        });
    }

}
