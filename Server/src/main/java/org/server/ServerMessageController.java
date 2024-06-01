package org.server;

import lombok.extern.slf4j.Slf4j;
import org.share.HeaderPacket;
import packetlist.afterregister.PacketForDM;
import packetlist.afterregister.PacketForUserMessage;
import packetlist.afterregister.PacketForUserNameChange;
import packetlist.beforeregister.PacketForUserExit;
import packetlist.serverpacket.PacketForServerNotice;
import packetlist.serverpacket.PacketForUserExitFromServer;
import packetlist.serverpacket.PacketForUserNameDuplication;
import packetlist.serverpacket.PacketForUserRegistrationAcceptance;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;
@Slf4j
public class ServerMessageController {
    private final ReentrantLock L1 = new ReentrantLock();

    private byte[] packetToByte(HeaderPacket packet) {
        byte[] header = packet.getHeaderBytes();
        byte[] body = packet.getBodyBytes();
        byte[] packetByte = new byte[header.length + body.length];
        System.arraycopy(header, 0, packetByte, 0, header.length);
        System.arraycopy(body, 0, packetByte, header.length, body.length);
        log.debug("ServerMessageController's packetToByte method is called / packetType : {}, packetBytesLength : {},  packetBytes {}", packet.getPacketType(), packetByte.length, packetByte);
        return packetByte;
    }

    public void sendClientMessageToAllClient(PacketForUserMessage messagePacket, ClientResisterManager clientResisterManager) {
        String name = messagePacket.getName();
        byte[] messageBytes = packetToByte(messagePacket);
        log.debug("ServerMessageController's sendClientMessageToAllClient method is called in server / messageBytesLength: {}, messageBytes : {}", messageBytes.length, messageBytes);
        for (HashMap.Entry<String, UserManager> entry : clientResisterManager.getClientsCopyMap().entrySet()) {
            UserManager userManager = entry.getValue();
            try {
                OutputStream outputStream = userManager.getSocket().getOutputStream();
                if (name.equals(entry.getKey())) {
                    userManager.addChatCount();
                    continue;
                }
                outputStream.write(messageBytes);
                outputStream.flush();
            } catch (IOException e) {
                clientResisterManager.removeUser(entry.getKey());
                log.error("Error while sending message to client: " + entry.getKey(), e);
            }
        }
    }

    public void userNameChange(PacketForUserNameChange packetForUserNameChange, ClientResisterManager clientResisterManager) {
        String name = packetForUserNameChange.getName();
        String changedName = packetForUserNameChange.getChangedName();
        log.debug("ServerMessageController's userNameChange method is called, name : {}, changedName : {}", name, changedName);
        PacketForServerNotice packetForServerNotice = new PacketForServerNotice(changedName);
        try {
            //
            UserManager userManager = clientResisterManager.getUserManager(name);
            OutputStream outputStream = userManager.getSocket().getOutputStream();
            //
            if (clientResisterManager.containsUser(changedName)) {
                PacketForUserNameDuplication packetForUserNameDuplication = new PacketForUserNameDuplication();
                byte[] duplicatedClientNameNoticeByte = packetToByte(packetForUserNameDuplication);
                log.debug("ServerMessageController's userNameChange method is called/ duplicatedClientNameNoticeByte's Length : {}, duplicatedClientNameNoticeByte : {}", duplicatedClientNameNoticeByte.length, duplicatedClientNameNoticeByte);
                outputStream.write(duplicatedClientNameNoticeByte);
            }
            clientResisterManager.removeUser(name);
            clientResisterManager.resisterUser(changedName, userManager);
            log.info("ServerMessageController's userNameChange method is called, userName is Changed / changedName : {} " ,changedName);
            serverNotifyToAllForNameChangeUser(packetForServerNotice, clientResisterManager); // 모든 이에게 전송
        } catch (IOException e) {
            clientResisterManager.removeUser(name);
        }
    }

    public void serverNotifyToAllForNameChangeUser(PacketForServerNotice packetForServerNotice, ClientResisterManager clientResisterManager) {
        byte[] serverNotice = packetToByte(packetForServerNotice);
        log.debug("ServerMessageController's serverNotifyToAll method is called in server/ serverNoticeBytes : {}", serverNotice);
        try {
            //
            for (HashMap.Entry<String, UserManager> entry : clientResisterManager.getClientsCopyMap().entrySet()) {
                UserManager userManager = entry.getValue();
                OutputStream outputStream = userManager.getSocket().getOutputStream();
                outputStream.write(serverNotice);
                outputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void serverNotifyForNewUser(PacketForUserRegistrationAcceptance packetForUserRegistrationAcceptance, ClientResisterManager clientResisterManager, String connectedUser) {
        byte[] serverNotice = packetToByte(packetForUserRegistrationAcceptance);
        //logger.debug("{}, {}", serverNotice.length , serverNotice); //packetToByte의 결과 굳이 출력할 필요가 있을까?
        try {
            //
            for (HashMap.Entry<String, UserManager> entry : clientResisterManager.getClientsCopyMap().entrySet()) {
                UserManager userManager = entry.getValue();
                OutputStream outputStream = userManager.getSocket().getOutputStream();
                outputStream.write(serverNotice);
                outputStream.flush();
            }
        } catch (IOException e) {
            clientResisterManager.removeUser(connectedUser);
        }
    }

    public void serverResponse(PacketForUserNameDuplication packetForUserNameDuplication, Socket socket) {
        byte[] serverNoticeForNameDuplication = packetToByte(packetForUserNameDuplication);
        log.debug("ServerMessageController's serverResponse method is called in server/ serverNoticeForNameDuplication : {}", serverNoticeForNameDuplication);
        try {
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(serverNoticeForNameDuplication);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendDirectMessage(PacketForDM packetForDM, ClientResisterManager clientResisterManager) {
        String sender = packetForDM.getSenderName();
        String receiver = packetForDM.getReceiverName();
        log.debug("ServerMessageController's sendDirectMessage method is called / sender : {} , receiver : {}", sender, receiver);
        byte[] directMessageBytes = packetToByte(packetForDM);
        log.debug("ServerMessageController's sendDirectMessage method is called/ directMessageBytes : {} ", directMessageBytes);
        try {
            UserManager userManagerForReceiver = clientResisterManager.getUserManager(receiver);
            if (userManagerForReceiver == null) {
                System.out.println("Entered User doesn't Exist: " + receiver);
            }
            OutputStream outputStream = userManagerForReceiver.getSocket().getOutputStream();
            outputStream.write(directMessageBytes);
            outputStream.flush();
            //
            UserManager userManager = clientResisterManager.getClientsCopyMap().getOrDefault(sender, null);
            userManager.addChatCount();
        } catch (IOException e) {
            clientResisterManager.removeUser(sender);
        }
    }

    public void disconnectClientForRegisterState(PacketForUserExit packetForUserExit, ClientResisterManager clientResisterManager) {
        String exitUserName = packetForUserExit.getName();
        log.debug("ServerMessageController's disconnectClient is called in server./ exitUserName : {}", exitUserName);
        PacketForUserExitFromServer packetForUserExitFromServer = new PacketForUserExitFromServer(exitUserName, clientResisterManager.getUserManager(exitUserName).getChatCount());
        log.debug("ServerMessageController's disconnectClient is called in server./ packetForUserExitFromServer : {}", packetForUserExitFromServer);
        serverNotifyToAllForExitUser(packetForUserExitFromServer, clientResisterManager);
        clientResisterManager.removeUser(exitUserName);
    }

    private void serverNotifyToAllForExitUser (PacketForUserExitFromServer packetForUserExitFromServer, ClientResisterManager clientResisterManager) {
        byte[] serverNotice = packetToByte(packetForUserExitFromServer);
        log.debug("ServerMessageController's serverNotifyToAllForExitUser method is called in server/ serverNoticeBytes : {}", serverNotice);
        try {
            //
            for (HashMap.Entry<String, UserManager> entry : clientResisterManager.getClientsCopyMap().entrySet()) {
                UserManager userManager = entry.getValue();
                OutputStream outputStream = userManager.getSocket().getOutputStream();
                outputStream.write(serverNotice);
                outputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
