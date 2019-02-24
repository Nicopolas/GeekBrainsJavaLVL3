package ru.geekbrains.java.two.chat.server.core;

import ru.geekbrains.java.two.library.Messages;
import ru.geekbrains.java.two.network.SocketThread;
import ru.geekbrains.java.two.network.SocketThreadListener;

import java.net.Socket;

public class ClientThread extends SocketThread {

    private boolean isAuthorized;
    private boolean isReconnecting;
    private String nickname;

    public ClientThread(SocketThreadListener listener, String name, Socket socket) {
        super(listener, name, socket);
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }

    public String getNickname() {
        return nickname;
    }

    public boolean isReconnecting() {
        return isReconnecting;
    }

    void reconnect() {
        isReconnecting = true;
        close();
    }

    void authorizeAccept(String nickname) {
        isAuthorized = true;
        this.nickname = nickname;
        sendMessage(Messages.getAuthAccept(nickname));
    }

    void authorizeError() {
        sendMessage(Messages.getAuthError());
        close();
    }

    void msgFormatError(String msg) {
        sendMessage(Messages.getMsgFormatError(msg));
        close();
    }
}
