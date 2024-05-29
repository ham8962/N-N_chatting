package org.server;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.share.HeaderPacket;
import packetlist.afterregister.PacketForFileInformationAndCheckReceiver;
import packetlist.afterregister.PacketForFileSending;
import packetlist.serverpacket.PacketForReceiverAbsent;
import packetlist.serverpacket.PacketForReceiverExistenceAndFileTransferSetting;

import java.io.IOException;
import java.io.OutputStream;

@Setter
@Slf4j
public class FileSendController {
    private String fileName;
    private String sender;
    private String receiver;
    private String filePath;
    private long fileSize;
    private int sendingCount;
    private int lastSendingCount;
    private byte[] buffer;

    private byte[] packetToByte(HeaderPacket packet) {
        byte[] header = packet.getHeaderBytes();
        byte[] body = packet.getBodyBytes();
        byte[] packetByte = new byte[header.length + body.length];
        System.arraycopy(header, 0, packetByte, 0, header.length);
        System.arraycopy(body, 0, packetByte, header.length, body.length);
       //logger.debug("FileSendController's packetToBytes method is called / packetBytes : {}", packetByte);
        return packetByte;
    }

    public void checkFileInformationAndReceiverExistence(PacketForFileInformationAndCheckReceiver packetForFileInformationAndCheckReceiver, ClientResisterManager clientResisterManager) {
        fileName = packetForFileInformationAndCheckReceiver.getFileName();
        sender = packetForFileInformationAndCheckReceiver.getSender();
        receiver = packetForFileInformationAndCheckReceiver.getReceiver();
        filePath = packetForFileInformationAndCheckReceiver.getFilePath();
        log.debug("FileSendController's checkFileInformationAndReceiverExistence method is called. / fileName : {}, sender : {}, receiver : {}, filepath: {}", fileName, sender, receiver, filePath);
        UserManager userManager = clientResisterManager.getClientsMap().get(sender);
        //
        if (clientResisterManager.containsUser(receiver)) {
            try {
                OutputStream outputStream = userManager.getSocket().getOutputStream();
                byte[] serverResponseForReceiverExistence = packetToByte(new PacketForReceiverExistenceAndFileTransferSetting(sender, receiver, filePath, fileName));
                log.debug("FileSendController's checkFileInformationAndReceiverExistence method is called. / serverResponseForReceiverExistenceByteLength : {}, serverResponseForReceiverExistenceBytes : {}", serverResponseForReceiverExistence.length, serverResponseForReceiverExistence);
                outputStream.write(serverResponseForReceiverExistence);
            } catch (IOException e) {
                clientResisterManager.removeUser(sender);
            }
        } else {
            try {
                OutputStream outputStream = userManager.getSocket().getOutputStream();
                byte[] serverResponseForReceiverAbsent = packetToByte(new PacketForReceiverAbsent());
                log.debug("FileSendController's checkFileInformationAndReceiverExistence method is called. / serverResponseForReceiverAbsentLength : {}, serverResponseForReceiverAbsentBytes : {}", serverResponseForReceiverAbsent.length, serverResponseForReceiverAbsent);
                outputStream.write(serverResponseForReceiverAbsent);
            } catch (IOException e) {
                clientResisterManager.removeUser(sender);
            }
        }

    }

    // 같은 수신자에게 2개 이상의 다른 파일을 어떻게 구별시켜줄까 >>  파일 이름 계속 보내서 받는 쪽에서 활용하도록 하기
    public void sendFileToClient(PacketForFileSending packetForFileSending, ClientResisterManager clientResisterManager) {
        log.debug("FileSendController's sendFileToClient method is called. / packetForFileSending's sendingCount : {}, lastSendingCount : {}", packetForFileSending.getSendingCount(), packetForFileSending.getLastSendingCount());
        log.debug("FileSendController's sendFileToClient method is called. / packetForFileSending's sender : {}, receiver : {}, fileName : {}", packetForFileSending.getSender(), packetForFileSending.getReceiver(), packetForFileSending.getFileName());
        //logger.debug("FileSendController's sendFileToClient method is called. / packetForFileSending's fileBytesBuffer'Length : {}, packetForFileSending's fileBytesBuffer : {}", packetForFileSending.getBuffer().length, packetForFileSending.getBuffer());
        try {
            UserManager userManager = clientResisterManager.getUserManager(packetForFileSending.getReceiver());
            log.debug("FileSendController's sendFileToClient method is called. /userManager : {}", userManager);
            OutputStream outputStream = userManager.getSocket().getOutputStream();
            outputStream.write(packetToByte(packetForFileSending));
            userManager.addChatCount();
            outputStream.flush();
        } catch (IOException e) {
            clientResisterManager.removeUser(packetForFileSending.getSender());
        }
    }
}
