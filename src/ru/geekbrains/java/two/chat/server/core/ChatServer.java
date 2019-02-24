package ru.geekbrains.java.two.chat.server.core;

import ru.geekbrains.java.two.library.Messages;
import ru.geekbrains.java.two.network.ServerSocketThread;
import ru.geekbrains.java.two.network.ServerSocketThreadListener;
import ru.geekbrains.java.two.network.SocketThread;
import ru.geekbrains.java.two.network.SocketThreadListener;

import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Vector;

public class ChatServer implements ServerSocketThreadListener, SocketThreadListener {

    private ServerSocketThread serverSocketThread;
    private final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss: ");
    private final ChatServerListener listener;
    private final Vector<SocketThread> clients = new Vector<>();

    public ChatServer(ChatServerListener listener) {
        this.listener = listener;
    }

    /**
     * ChatServer actions
     * */

    public void start(int port) {
        if (serverSocketThread != null && serverSocketThread.isAlive())
            putLog("Server is already running");
        else
            serverSocketThread = new ServerSocketThread(this, "ChatServer", port, 2000);
    }

    public void stop() {
        if (serverSocketThread == null || !serverSocketThread.isAlive())
            putLog("Server is stopped");
        else
            serverSocketThread.interrupt();
    }

    void putLog(String msg) {
        msg = dateFormat.format(System.currentTimeMillis()) + Thread.currentThread().getName() + ": " + msg;
        listener.onChatServerMsg(this, msg);

    }

    private String getUsers() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < clients.size(); i++) {
            ClientThread client = (ClientThread) clients.get(i);
            if (!client.isAuthorized()) continue;
            sb.append(client.getNickname()).append(Messages.DELIMITER);
        }
        return sb.toString();
    }

    /**
     * ServerSocket Thread listener implementation
     * */

    @Override
    public void onServerThreadStart(ServerSocketThread thread) {
        SqlClient.connect();
    }

    @Override
    public void onServerSocketCreated(ServerSocketThread thread, ServerSocket server) {
        putLog("server socket created");
    }

    @Override
    public void onSocketAccepted(ServerSocketThread thread, Socket socket) {
        putLog("client connected:" + socket);
        String threadName = "SocketThread: " + socket.getInetAddress() + ":" + socket.getPort();
        new ClientThread(this, threadName, socket);
    }

    @Override
    public void onAcceptTimeout(ServerSocketThread thread, ServerSocket server) {
        //putLog("accept timeout");
    }

    @Override
    public void onServerThreadException(ServerSocketThread thread, Exception e) {
        putLog("Exception: " + e.getClass().getName() + ": " + e.getMessage());
    }

    @Override
    public void onServerThreadStop(ServerSocketThread thread) {
        for (int i = 0; i < clients.size(); i++) {
            clients.get(i).close();
        }
        SqlClient.disconnect();
    }

    /**
     * SocketThread listener methods
     * */

    @Override
    public synchronized void onStartSocketThread(SocketThread thread, Socket socket) {
        putLog("SocketThread started");
    }

    @Override
    public synchronized void onStopSocketThread(SocketThread thread) {
        ClientThread client = (ClientThread) thread;
        clients.remove(thread);
        if (client.isAuthorized() && !client.isReconnecting()) {
            sendToAuthorizedClients(Messages.getTypeBroadcast(
                            "Server", client.getNickname() + " disconnected"));
            sendToAuthorizedClients(Messages.getUserList(getUsers()));
        }
    }

    @Override
    public synchronized void onSocketThreadIsReady(SocketThread thread, Socket socket) {
        clients.add(thread);
        putLog("Client connected");
    }

    @Override
    public synchronized void onReceiveString(SocketThread thread, Socket socket, String value) {
        ClientThread client = (ClientThread) thread;
        if (client.isAuthorized()) {
            handleAuthMsg(client, value);
        } else {
            handleNonAuthMsg(client, value);
        }
    }

    private void handleNonAuthMsg(ClientThread newClient, String msg) {
        String[] arr = msg.split(Messages.DELIMITER);
        if (arr.length != 3 || !arr[0].equals(Messages.AUTH_REQUEST)) {
            newClient.msgFormatError(msg);
            return;
        }
        String login = arr[1];
        String password = arr[2];
        String nickname = SqlClient.getNickname(login, password);

        if (nickname == null) {
            putLog("Invalid login/password: login = '" +
                    login + "', password = '" + password + "'");
            newClient.authorizeError();
            return;
        }
//        newClient.authorizeAccept(nickname);

        // deny duplicate nicknames dummy
//        if (clients.contains(newClient)) return;
        ClientThread client = findClientByNick(nickname);
        newClient.authorizeAccept(nickname);
        if (client == null) {
            sendToAuthorizedClients(Messages.getTypeBroadcast("Server", nickname + " connected"));
        } else {
            client.reconnect();
            clients.remove(client);
        }

        sendToAuthorizedClients(Messages.getUserList(getUsers()));
    }

    private synchronized ClientThread findClientByNick(String nickname) {
        for (int i = 0; i < clients.size(); i++) {
            ClientThread client = (ClientThread) clients.get(i);
            if (!client.isAuthorized()) continue;
            if (client.getNickname().equals(nickname))
                return client;
        }
        return null;
    }

    private void handleAuthMsg(ClientThread client, String msg) {
        String[] arr = msg.split(Messages.DELIMITER);
        String msgType = arr[0];
        switch (msgType) {
            case Messages.TYPE_CLIENT_BCAST:
                sendToAuthorizedClients(Messages.getTypeBroadcast(client.getNickname(), arr[1]));
                break;
            default:
                client.msgFormatError(msg);
        }
    }

    private void sendToAuthorizedClients(String value) {
        for (int i = 0; i < clients.size(); i++) {
            ClientThread client = (ClientThread) clients.get(i);
            if (!client.isAuthorized()) continue;
            client.sendMessage(value);
        }
    }

    @Override
    public synchronized void onSocketThreadException(SocketThread thread, Exception e) {
        putLog("Exception: " + e.getClass().getName() + ": " + e.getMessage());
    }
}
