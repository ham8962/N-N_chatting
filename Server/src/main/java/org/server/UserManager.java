package org.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.net.Socket;
@AllArgsConstructor
@Getter
@Setter
public class UserManager {
    private Socket socket;
    private int chatCount = 0;

    public void addChatCount() {
        chatCount++;
    }
}
