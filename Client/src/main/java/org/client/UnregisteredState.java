package org.client;

import lombok.extern.slf4j.Slf4j;
import org.share.HeaderPacket;
import packetlist.beforeregister.PacketForUserResister;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class UnregisteredState implements ClientState {
    private Client client;
    private Map<String, Command> unregisteredUserCommandMap = new HashMap<>();
    private final Socket socket;


    public UnregisteredState(Client client, Socket socket) {
        this.socket = socket;
        this.client = client;
        initUnregisteredUserCommandMap();
    }

    @Override
    public boolean handleCommand(String command) {
        //logger.info("user command : {}", command);
        return commandExecute(command);
    }

    @Override
    public void sendPacketToBytes(HeaderPacket packet) {
        try {
            var outputStream = socket.getOutputStream();
            byte[] headerBytes = packet.getHeaderBytes();
            log.debug("header of UnregisteredClient's sending packet : headerBytesLength : {}, packetType: {}, headerBytes : {}", headerBytes.length, packet.getPacketType(), headerBytes);
            byte[] bodyBytes = packet.getBodyBytes();
            log.debug("body of UnregisteredClient's sending packet : {}, {}", bodyBytes.length, bodyBytes);
            byte[] sendPacketBytes = new byte[headerBytes.length + bodyBytes.length];
            System.arraycopy(headerBytes, 0, sendPacketBytes, 0, headerBytes.length);
            System.arraycopy(bodyBytes, 0, sendPacketBytes, headerBytes.length, bodyBytes.length);
            log.debug("sendingPacket of UnregisteredClient: {}, {}", sendPacketBytes.length, sendPacketBytes);
            outputStream.write(sendPacketBytes);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean commandExecute(String inputCommand) {
        String[] commandList = inputCommand.split(" ");
        log.debug("user command word : {}", commandList);
        Command command = unregisteredUserCommandMap.get(commandList[0]);
        if (command != null) {
            return command.execute(commandList);
        } else {
            System.out.println("Invalid commend, Please enter Correct Command");
        }
        return true;
    }

    private void initUnregisteredUserCommandMap() {
        unregisteredUserCommandMap.put("/register", args -> {
            List<String> argsList = Arrays.asList(args);
            List<String> userNameSegmentList = argsList.subList(1, argsList.size());
            log.debug("UnregisterState userNameSegmentList: {}", argsList);
            String userName = String.join(" ", userNameSegmentList);
            client.setUserName(userName);
            log.info("UnregisterState userName : {}", client.getUserName());
            PacketForUserResister packetForUserResister = new PacketForUserResister(client.getUserName());
            //logger.debug("UnregisterState try to register : {}", packetForUserResister);
            sendPacketToBytes(packetForUserResister);
            return true;
        });

        unregisteredUserCommandMap.put("/exit", args -> {
            System.out.println("TURN OFF");
            log.info("UnregisterState user exit");
            return false;
        });
    }

}
