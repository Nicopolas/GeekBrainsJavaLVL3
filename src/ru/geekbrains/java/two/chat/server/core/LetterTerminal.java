package ru.geekbrains.java.two.chat.server.core;

class LetterTerminal implements Runnable, ExecutorServiceInterface {
    private String str;
    private int iteration = 0;
    ChatServer chatServer;

    public LetterTerminal(String str, ChatServer chatServer) {
        this.str = str;
        this.chatServer = chatServer;
    }

    @Override
    public void run() {
        while (iteration != 5) {
            try {
                synchronized (lock) {
                    while (!StringEnum.get().equals(str)) {
                        lock.wait();
                    }
                    chatServer.putLog(str);
                    StringEnum.next();
                    iteration++;
                    lock.notifyAll();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
