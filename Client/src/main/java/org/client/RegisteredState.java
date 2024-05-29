package org.client;

import lombok.extern.slf4j.Slf4j;
import org.share.HeaderPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import packetlist.afterregister.*;
import packetlist.beforeregister.PacketForUserExit;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.*;

@Slf4j
public class RegisteredState implements ClientState {
    private Client client;
    private Map<String, Command> registeredUserCommandMap = new HashMap<>();
    private final Socket socket;

    public RegisteredState(Client client, Socket socket) {
        this.client = client;
        this.socket = socket;
        registeredUserCommandMapInit();
    }

    @Override
    public boolean handleCommand(String command) {
        return commandExecute(command);
    }

    @Override
    public void sendPacketToBytes(HeaderPacket packet) {
        try {
            var outputStream = socket.getOutputStream();
            byte[] headerBytes = packet.getHeaderBytes();
            log.debug("sendPacketToBytes method of RegisterState is called. headerBytes of RegisteredClient's sending packet / headerBytesLength : {}, packetType : {}, headerBytes : {}", headerBytes.length, packet.getPacketType(), headerBytes);
            byte[] bodyBytes = packet.getBodyBytes();
            log.debug("sendPacketToBytes method of RegisterState is called. bodyBytes of RegisteredClient's sending packet / bodyBytesLength : {}, bodyBytes: {}", bodyBytes.length, bodyBytes);
            byte[] sendPacketBytes = new byte[headerBytes.length + bodyBytes.length];
            System.arraycopy(headerBytes, 0, sendPacketBytes, 0, headerBytes.length);
            System.arraycopy(bodyBytes, 0, sendPacketBytes, headerBytes.length, bodyBytes.length);
            log.debug("sendPacketToBytes method of RegisterState is called. sendingPacket RegisteredClient/ packetLength : {}, packetBytes: {}", sendPacketBytes.length, sendPacketBytes);
            outputStream.write(sendPacketBytes);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean commandExecute(String inputCommand) {
        String[] args = inputCommand.split(" ");
        Command command = registeredUserCommandMap.getOrDefault(args[0], registeredUserCommandMap.get("defalut"));
        if (command != null) {
            return command.execute(args);
        } else {
            System.out.println("Invalid commend, Please enter Correct Command");
        }
        return true;
    }

    private void registeredUserCommandMapInit() {
        registeredUserCommandMap.put("defalut", args -> {
            List<String> messagePieceList = Arrays.asList(args);
            String message = String.join(" ", messagePieceList);
            log.debug("RegisteredUser's Message : {}", message);
            PacketForUserMessage packetForUserMessage = new PacketForUserMessage(message, client.getUserName());
            sendPacketToBytes(packetForUserMessage);
            return true;
        });

        registeredUserCommandMap.put("/changeName", args -> {
            List<String> argsList = Arrays.asList(args);
            List<String> slicedArgs = argsList.subList(1, argsList.size());
            String newName = String.join(" ", slicedArgs);
            log.info( "RegisteredUser's changedName : {}", newName);
            PacketForUserNameChange packetForUserNameChange = new PacketForUserNameChange(client.getUserName(), newName);
            sendPacketToBytes(packetForUserNameChange);
            return true;
        });

        registeredUserCommandMap.put("/w", args -> {
            // args 배열에서 명령어를 제외한 나머지를 하나의 문자열로 합칩니다.
            String receiverAndDirectMessage = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            // ","를 기준으로 문자열을 분리하여 수신자 이름과 메세지 내용을 구분합니다.
            int firstCommaIndex = receiverAndDirectMessage.indexOf(',');
            if (firstCommaIndex == -1) { // ','가 없는 경우
                System.out.println("메시지 형식이 올바르지 않습니다. '/w (수신자 이름),(메세지 내용)' 형식으로 입력해주세요.");
                return false;
            }
            // ','를 기준으로 수신자 이름과 메시지 내용을 분리
            String receiverName = receiverAndDirectMessage.substring(0, firstCommaIndex).trim();
            log.debug("registeredUser's directMessage / receiver : {}", receiverName);
            String directMessage = receiverAndDirectMessage.substring(firstCommaIndex + 1).trim();
            log.debug("registeredUser's directMessage/ directMessage : {}", directMessage);
            // 패킷 생성 및 전송
            PacketForDM packetForDM = new PacketForDM(client.getUserName(), receiverName, directMessage);
            sendPacketToBytes(packetForDM);
            return true;
        });

        registeredUserCommandMap.put("/exit", args -> {
            PacketForUserExit packetForUserExit = new PacketForUserExit(client.getUserName());
            sendPacketToBytes(packetForUserExit);
            return false;
        });

        registeredUserCommandMap.put("/f", args -> {
            String filePathAndReceiver = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            int firstCommaIndex = filePathAndReceiver.indexOf(" ");
            if (firstCommaIndex == -1) {
                System.out.println("메시지 형식이 올바르지 않습니다. '/f (파일 경로) (수신자)' 형식으로 입력해주세요.");
                return false;
            }
            String filePath = filePathAndReceiver.substring(0, firstCommaIndex).trim();
            log.debug("filePath of registeredUser's fileSending {}", filePath);
            String receiver = filePathAndReceiver.substring(firstCommaIndex + 1).trim();
            log.debug("fileReceiver of registeredUser's fileSending {}", receiver);
            File file = new File(filePath);
            if (file.exists()) {
                PacketForFileInformationAndCheckReceiver packetForFileInformationAndCheckReceiver = new PacketForFileInformationAndCheckReceiver(file.getName(),
                        client.getUserName(),
                        receiver,
                        filePath,
                        file.length()
                        );
                sendPacketToBytes(packetForFileInformationAndCheckReceiver);
            } else {
                System.out.println("File does not exist");
            }
            return true;
        });
    }
}
