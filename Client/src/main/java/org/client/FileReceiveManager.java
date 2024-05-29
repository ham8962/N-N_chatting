package org.client;

import lombok.extern.slf4j.Slf4j;
import packetlist.afterregister.PacketForFileSending;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
@Slf4j
public class FileReceiveManager {
    private FileOutputStream fileOutputStream;
    private BufferedOutputStream bufferedOutputStream;
    private String savePath;
    public FileReceiveManager(String savePath) {
        this.savePath = savePath;
    }

    public void receiveFilePacket(PacketForFileSending packetForFileSending) {
        log.debug("FileReceiver's receiveFilePacket method is called. / fileName : {} ", packetForFileSending.getFileName());
        File file = new File(savePath, packetForFileSending.getFileName());
        try (FileOutputStream fileOutputStream = new FileOutputStream(file, true);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream)) {
                log.debug("FileReceiver's receiveFilePacket method is called. / filePacketLength : {}, fileBytes : {}", packetForFileSending.getBuffer().length, packetForFileSending.getBuffer());
                bufferedOutputStream.write(packetForFileSending.getBuffer());
            if (packetForFileSending.getSendingCount() == packetForFileSending.getLastSendingCount()) {
                bufferedOutputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
