package ru.geekbrains.java.two.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ServerSocketThread extends Thread {
    private final int port;
    private final int timeout;
    private ServerSocketThreadListener listener;

    public ServerSocketThread(ServerSocketThreadListener listener, String name, int port, int timeout) {
        super(name);
        this.port = port;
        this.timeout = timeout;
        this.listener = listener;
        start();
    }

    @Override
    public void run() {
        listener.onServerThreadStart(this);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setSoTimeout(timeout);
            listener.onServerSocketCreated(this, serverSocket);
            while (!isInterrupted()) {
                Socket socket;
                try {
                    socket = serverSocket.accept();
                } catch (SocketTimeoutException e) {
                    listener.onAcceptTimeout(this, serverSocket);
                    continue;
                }
                listener.onSocketAccepted(this, socket);
            }
        } catch (IOException e) {
            listener.onServerThreadException(this, e);
        } finally {
            listener.onServerThreadStop(this);
        }
    }
}
