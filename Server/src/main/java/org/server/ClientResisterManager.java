package org.server;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public class ClientResisterManager {
    private HashMap<String, UserManager> clientsMap = new HashMap<>();
    private final ReentrantLock L2 = new ReentrantLock();

    public void resisterUser(String userName, UserManager userManager) {
        L2.lock();
        try {
            clientsMap.put(userName, userManager);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            L2.unlock();
        }
    }

    public void removeUser(String userName) {
        L2.lock();
        try {
            clientsMap.remove(userName);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            L2.unlock();
        }
    }

    public boolean containsUser(String userName) {
        L2.lock();
        try {
            return clientsMap.containsKey(userName);
        } finally {
            L2.unlock();
        }
    }

    public UserManager getUserManager(String userName) {
        L2.lock();
        try {
            return clientsMap.get(userName);
        } finally {
            L2.unlock();
        }
    }

    public HashMap<String, UserManager> getClientsCopyMap() {
        L2.lock();
        try {
            return new HashMap<>(this.clientsMap);
        } finally {
            L2.unlock();
        }
    }
}
