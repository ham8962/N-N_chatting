package org.client;

import org.share.HeaderPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import packetlist.afterregister.PacketForFileSending;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

public class FileTransferManager {
    private final Socket socket;
    private final int sendingByteSize = 100000;
    private String sender;
    private String receiver;
    private String fileName;
    private String filePath;
    private final Logger logger;

    public FileTransferManager(Socket socket) {
        this.socket = socket;
        this.logger = LoggerFactory.getLogger(FileTransferManager.class);
    }

    public void set(String sender, String receiver, String filePath, String fileName) {
        logger.debug("FileTransferManager's set method is called / sender: {}, receiver : {}, filepath:{}, fileName :{}", sender, receiver, filePath, fileName);
        this.sender = sender;
        this.receiver = receiver;
        this.filePath = filePath;
        this.fileName = fileName;
    }

    private void sendPacketToBytes(HeaderPacket packet) {
        try {
            var outputStream = socket.getOutputStream();
            byte[] headerBytes = packet.getHeaderBytes();
            // ServerReceiver의 run()메서드 log와 비교 할 것
            logger.debug("FileTransferManager's sendPacketToBytes method is called / headerBytesLength : {}, PacketType: {}, headerBytes: {}", headerBytes.length, packet.getPacketType(), headerBytes);
            byte[] bodyBytes = packet.getBodyBytes();
            logger.debug("FileTransferManager's sendPacketToBytes method is called / bodyBytesLength : {}, bodyBytes: {}", bodyBytes.length, bodyBytes);
            byte[] sendPacketBytes = new byte[headerBytes.length + bodyBytes.length];
            System.arraycopy(headerBytes, 0, sendPacketBytes, 0, headerBytes.length);
            System.arraycopy(bodyBytes, 0, sendPacketBytes, headerBytes.length, bodyBytes.length);
            logger.debug("");
            outputStream.write(sendPacketBytes);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendFile() {
        File file = new File(filePath);
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] buffer = new byte[sendingByteSize];
            int byteRead;
            int sendingCount = 0;
            int lastSendingCount = (int) Math.ceil((double) file.length() / sendingByteSize);
            logger.debug("FileTransferManager's sendFile method is called / lastSendingCount : {}", lastSendingCount);
            while ((byteRead = fileInputStream.read(buffer)) > 0) {
                byte[] realSendingBuffer = new byte[byteRead];
                System.arraycopy(buffer, 0, realSendingBuffer, 0, byteRead);
                logger.debug("FileTransferManager's sendFile method is called / SendingCount : {}", sendingCount);
                //Server의 FileSendController의 sendFiletClient와 비교할 것, 순수한 파일 분할 바이트
                //logger.debug("FileTransferManager's sendFile method is called / file's realSendingBufferLength : {}, file's realSendingBuffer : {}", realSendingBuffer.length, realSendingBuffer);
                logger.debug("FileTransferManager's sendFile method is called / fileName : {}, sender :{}, receiver : {}" , file.getName(), sender, receiver );
                PacketForFileSending packetForFileSending = new PacketForFileSending(sendingCount, lastSendingCount, realSendingBuffer, file.getName(), sender, receiver);
                sendPacketToBytes(packetForFileSending);
                sendingCount++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
