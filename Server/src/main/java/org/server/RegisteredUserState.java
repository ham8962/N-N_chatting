package org.server;

import lombok.extern.slf4j.Slf4j;
import org.share.HeaderPacket;
import org.share.PacketType;
import packetlist.afterregister.*;
import packetlist.beforeregister.PacketForUserExit;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RegisteredUserState implements UserState {
    private User user;
    private Map<PacketType, PacketIdentifier> registeredUserPacketIdentifierMap = new HashMap<>();
    private Map<PacketType, PacketHandler> registeredUserPacketHandlerMap = new HashMap<>();

    public RegisteredUserState(User user) {
        this.user = user;
        registeredUserPacketIdentifierMapInit();
        registeredUserPacketHandlerMapInit();
    }

    @Override
    public boolean handlePacket(PacketType packetType, byte[] receivedBodyBytes) {
        if(packetType == PacketType.SendFile) {
            log.debug("handlePacket method of RegisteredUserState is called in Server / packetType : {}, receivedBodyBytesLength : {}", packetType,receivedBodyBytes.length);
        } else {
            log.debug("handlePacket method of RegisteredUserState is called in Server / packetType : {}, receivedBodyBytesLength : {}, receivedBodyBytes : {}", packetType, receivedBodyBytes.length, receivedBodyBytes);
        }
        PacketIdentifier packetIdentifier = registeredUserPacketIdentifierMap.getOrDefault(packetType, null);
        PacketHandler packetHandler = registeredUserPacketHandlerMap.getOrDefault(packetType, null);
        HeaderPacket packet = packetIdentifier.packetChecker(receivedBodyBytes);
        return packetHandler.handle(packet);
    }

    private void registeredUserPacketIdentifierMapInit() {
        registeredUserPacketIdentifierMap.put(PacketType.Exit, bodyBytes -> PacketForUserExit.of(bodyBytes));
        registeredUserPacketIdentifierMap.put(PacketType.SendMessageToALL, bodyBytes -> PacketForUserMessage.of(bodyBytes));
        registeredUserPacketIdentifierMap.put(PacketType.ChangeUserName, bodyBytes -> PacketForUserNameChange.of(bodyBytes));
        registeredUserPacketIdentifierMap.put(PacketType.DirectMessage, bodyBytes -> PacketForDM.of(bodyBytes));
        registeredUserPacketIdentifierMap.put(PacketType.FileInformation, bodyBytes -> PacketForFileInformationAndCheckReceiver.of(bodyBytes));
        registeredUserPacketIdentifierMap.put(PacketType.SendFile, bodyBytes -> PacketForFileSending.of(bodyBytes));
    }

    private void registeredUserPacketHandlerMapInit() {
        ServerMessageController serverMessageController = new ServerMessageController();
        FileSendController fileSendController = new FileSendController();

        registeredUserPacketHandlerMap.put(PacketType.Exit, packet -> {
            PacketForUserExit packetForUserExit = (PacketForUserExit) packet;
            //logger.debug("{}", packetForUserExit.getName());
            serverMessageController.disconnectClientForRegisterState(packetForUserExit, user.getClientResisterManager());
            return false;
        });

        registeredUserPacketHandlerMap.put(PacketType.SendMessageToALL, packet -> {
            PacketForUserMessage packetForUserMessage = (PacketForUserMessage) packet;
            //logger.debug("{}", packetForUserMessage);
            serverMessageController.sendClientMessageToAllClient(packetForUserMessage, user.getClientResisterManager());
            return true;
        });

        registeredUserPacketHandlerMap.put(PacketType.ChangeUserName, packet -> {
            PacketForUserNameChange packetForUserNameChange = (PacketForUserNameChange) packet;
            //logger.debug();
            serverMessageController.userNameChange(packetForUserNameChange, user.getClientResisterManager());
            return true;
        });

        registeredUserPacketHandlerMap.put(PacketType.DirectMessage, packet -> {
            PacketForDM packetForDM = (PacketForDM) packet;
            //logger.debug();
            serverMessageController.sendDirectMessage(packetForDM, user.getClientResisterManager());
            return true;
        });

        registeredUserPacketHandlerMap.put(PacketType.FileInformation, packet -> {
            PacketForFileInformationAndCheckReceiver packetForFileInformationAndCheckReceiver = (PacketForFileInformationAndCheckReceiver) packet;
            //logger.debug();
            fileSendController.checkFileInformationAndReceiverExistence(packetForFileInformationAndCheckReceiver, user.getClientResisterManager());
            return true;
        });

        registeredUserPacketHandlerMap.put(PacketType.SendFile, packet -> {
            PacketForFileSending packetForFileSending = (PacketForFileSending) packet;
            //logger.debug(packetForFileSending.get);
            fileSendController.sendFileToClient(packetForFileSending, user.getClientResisterManager());
            return true;
        });
    }
}