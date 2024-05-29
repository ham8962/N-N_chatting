package org.server;

import lombok.extern.slf4j.Slf4j;
import org.share.HeaderPacket;
import org.share.PacketType;
import packetlist.beforeregister.PacketForUserExit;
import packetlist.beforeregister.PacketForUserResister;
import packetlist.serverpacket.PacketForUserNameDuplication;
import packetlist.serverpacket.PacketForUserRegistrationAcceptance;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
@Slf4j
public class UnregisteredUserState implements UserState {
    private User user;
    private final Socket socket;
    private final ReentrantLock lockForClientManaging = new ReentrantLock();
    private Map<PacketType, PacketIdentifier> unregisteredUserPacketIdentifierMap = new HashMap<>();
    private Map<PacketType, PacketHandler> unregisteredUserPacketHandlerMap = new HashMap<>();

    public UnregisteredUserState(User user, Socket socket) {
        this.user = user;
        this.socket = socket;
        unregisteredUserPacketIdentifierMapInit();
        unregisteredUserPacketHandlerMapInit();
    }

    @Override
    public boolean handlePacket(PacketType packetType, byte[] receivedBodyBytes) {
        log.debug("handlePacket method of UnregisteredUserState in ServerApplication is called. / packetType : {}, receivedBodyBytes : {}", packetType, receivedBodyBytes);
        PacketIdentifier packetIdentifier = unregisteredUserPacketIdentifierMap.getOrDefault(packetType, null);
        PacketHandler packetHandler = unregisteredUserPacketHandlerMap.getOrDefault(packetType, null);
        HeaderPacket packet = packetIdentifier.packetChecker(receivedBodyBytes);
        return packetHandler.handle(packet);
    }

    private void unregisteredUserPacketIdentifierMapInit() {
        unregisteredUserPacketIdentifierMap.put(PacketType.Resister, bodyBytes -> PacketForUserResister.of(bodyBytes));
        unregisteredUserPacketIdentifierMap.put(PacketType.Exit, bodyBytes -> PacketForUserExit.of(bodyBytes));
    }

    private void unregisteredUserPacketHandlerMapInit() {
        ServerMessageController serverMessageController = new ServerMessageController();
        unregisteredUserPacketHandlerMap.put(PacketType.Resister, packet -> {
            PacketForUserResister packetForUserResister = (PacketForUserResister) packet;
            log.debug("handle method(lambda) is called in serverApplication. / UnregisteredUserState UserName which wants to register : {} ", packetForUserResister.getUserName());
            if(userNameChecker(packetForUserResister)) {
                user.setUserState(new RegisteredUserState(user));
            }
            return true;
        });

        unregisteredUserPacketHandlerMap.put(PacketType.Exit, packet -> {
            PacketForUserExit packetForUserExit = (PacketForUserExit) packet;
            log.info("SeverReceiver is closed");
            return false;
        });
    }

    private boolean userNameChecker(PacketForUserResister packetForUserResister) {
        ServerMessageController serverMessageController = new ServerMessageController();
        //
        if (user.getClientResisterManager().containsUser(packetForUserResister.getUserName())) {
            serverMessageController.serverResponse(new PacketForUserNameDuplication(), socket);
            return false;
        }
        UserManager userManager = new UserManager(socket, 0);
        lockForClientManaging.lock();
        try {
            //
            user.getClientResisterManager().resisterUser(packetForUserResister.getUserName(), userManager);
            serverMessageController.serverNotifyForNewUser(new PacketForUserRegistrationAcceptance(packetForUserResister.getUserName()), user.getClientResisterManager(), packetForUserResister.getUserName());
        } finally {
            lockForClientManaging.unlock();
        }
        return true;
    }
}

