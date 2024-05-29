package org.server;

import lombok.extern.slf4j.Slf4j;
import org.share.HeaderPacket;
import org.share.PacketType;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

@Slf4j
public class ServerReceiver extends Thread {
    private final Socket socket;
    protected String clientName;
    private ClientResisterManager clientResisterManager;
    //private final ReentrantLock lockForClientManaging = new ReentrantLock();

    //private Map<PacketType, PacketIdentifier> packetIdentifierMap = new HashMap<>();
    //private Map<PacketType, PacketHandler> packetHandlerMap = new HashMap<>();

    ServerReceiver(Socket socket, ClientResisterManager clientResisterManager) {
        this.socket = socket;
        this.clientResisterManager = clientResisterManager;
    }

    @Override
    public void run() {
        InputStream inputStream;
        PacketProcessor packetProcessor = new PacketProcessor();
        User user = new User(socket, clientResisterManager);
        try {
            inputStream = socket.getInputStream();
            while (true) {
                byte[] receivedHeaderBytesFromClient = new byte[8];
                inputStream.read(receivedHeaderBytesFromClient);
                log.debug("headerPacket of ServerReceiver's receivedPacket. / headerPacketLength : {}, packetType : {}, headerPacketBytes {}", receivedHeaderBytesFromClient.length, HeaderPacket.byteToPacketType(receivedHeaderBytesFromClient), receivedHeaderBytesFromClient);
                // 패킷 및 바디 검증
                boolean isValid = packetProcessor.processPacket(receivedHeaderBytesFromClient);
                if (!isValid) {
                    break;
                }
                PacketType packetType = HeaderPacket.byteToPacketType(receivedHeaderBytesFromClient);
                log.debug("packetType of ServerReceiver's receivedPacket : {}", packetType);
                int bodyLength = HeaderPacket.byteToBodyLength(receivedHeaderBytesFromClient);
                byte[] receivedBodyFromClient = new byte[bodyLength];
                inputStream.read(receivedBodyFromClient);
                //logger.debug("bodyLength and body of ServerReceiver's receivedPacket :{}, {}", receivedBodyFromClient.length, receivedBodyFromClient); // 이미지 파일 때문에 주석 처리 해 놓음
                if (!user.handlePacket(packetType, receivedBodyFromClient)) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("[" + clientName + "Disconnected]");
            clientResisterManager.removeUser(clientName);
            log.info("{}", clientResisterManager.getClientsMap());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /*private void userNameChecker(PacketForUserResister packetForUserResister) {
        ServerMessageController serverMessageController = new ServerMessageController();
        if (clientResisterManager.getClientsMap().containsKey(packetForUserResister.getUserName())) {
            clientName = null;
            serverMessageController.serverResponse(new PacketForUserNameDuplication("Duplicated name, Please enter another Name"), socket);
            //setUserNameCheckerFlag(false);
        }
        UserManager userManager = new UserManager(socket, 0);
        lockForClientManaging.lock();
        try {
            clientResisterManager.resisterUser(packetForUserResister.getUserName(), userManager);
        } finally {
            lockForClientManaging.unlock();
        }
        serverMessageController.serverNotifyForNewUser(new PacketForUserRegistrationAcceptance(packetForUserResister.getUserName() + "is Connected"), clientResisterManager);
        System.out.println("[" + packetForUserResister.getUserName() + "is Connected" + "]");
        clientName = packetForUserResister.getUserName();
        //setUserNameCheckerFlag(true);
    }

    private void initPacketMap() {
        packetIdentifierMap.put(PacketType.Resister, bodyBytes -> PacketForUserResister.of(bodyBytes));
        packetIdentifierMap.put(PacketType.Exit, bodyBytes -> PacketForUserExit.of(bodyBytes));
        packetIdentifierMap.put(PacketType.SendMessageToALL, bodyBytes -> PacketForUserMessage.of(bodyBytes));
        packetIdentifierMap.put(PacketType.ChangeUserName, bodyBytes -> PacketForUserNameChange.of(bodyBytes));
        packetIdentifierMap.put(PacketType.Server_Notice, bodyBytes -> PacketForServerNotice.of(bodyBytes));
        packetIdentifierMap.put(PacketType.DirectMessage, bodyBytes -> PacketForDM.of(bodyBytes));
        packetIdentifierMap.put(PacketType.FileInformation, bodyBytes -> PacketForFileInformationAndCheckReceiver.of(bodyBytes));
        packetIdentifierMap.put(PacketType.SendFile, bodyBytes -> PacketForFileSending.of(bodyBytes));
    }

    private void initPacketHandlerMap() {
        ServerMessageController serverMessageController = new ServerMessageController();
        //FileSendController fileSendController = new FileSendController();

        packetHandlerMap.put(PacketType.Resister, packet -> {
            PacketForUserResister packetForUserResister = (PacketForUserResister) packet;
            userNameChecker(packetForUserResister);
            return true;
        });
        packetHandlerMap.put(PacketType.SendMessageToALL, packet -> {
            PacketForUserMessage packetForUserMessage = (PacketForUserMessage) packet;
            serverMessageController.sendClientMessageToAllClient(packetForUserMessage, clientResisterManager);
            return true;
        });
        packetHandlerMap.put(PacketType.ChangeUserName, packet -> {
            PacketForUserNameChange packetForUserNameChange = (PacketForUserNameChange) packet;
            serverMessageController.userNameChange(packetForUserNameChange, clientResisterManager);
            return true;
        });
        packetHandlerMap.put(PacketType.Server_Notice, packet -> {
            PacketForServerNotice packetForServerNotice = (PacketForServerNotice) packet;
            serverMessageController.serverNotifyToAll(packetForServerNotice, clientResisterManager);
            return true;
        });
        packetHandlerMap.put(PacketType.DirectMessage, packet -> {
            PacketForDM packetForDM = (PacketForDM) packet;
            serverMessageController.sendDirectMessage(packetForDM, clientResisterManager);
            return true;
        });
        /*
        packetHandlerMap.put(PacketType.FileInformation, packet -> {
            PacketForFileInformationAndCheckReceiver packetForFileInformation = (PacketForFileInformationAndCheckReceiver) packet;
            fileSendController.checkFileInformation(packetForFileInformation, clientResisterManager);
            return true;
        });
        packetHandlerMap.put(PacketType.SendFile, packet -> {
            PacketForFileSending packetForFileSending = (PacketForFileSending) packet;
            fileSendController.sendFileToClient(packetForFileSending, clientResisterManager);
            return true;
        });
        packetHandlerMap.put(PacketType.Exit, packet -> {
            PacketForUserExit packetForUserExit = (PacketForUserExit) packet;
            serverMessageController.disconnectClient(packetForUserExit, clientResisterManager);
            return false;
        });
    }*/
}









