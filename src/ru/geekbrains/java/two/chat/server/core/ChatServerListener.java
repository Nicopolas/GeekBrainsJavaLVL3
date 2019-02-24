package ru.geekbrains.java.two.chat.server.core;

public interface ChatServerListener {

    void onChatServerMsg(ChatServer server, String msg);

}
