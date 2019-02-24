package ru.geekbrains.java.two.chat.client;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class HistoryLogHelper {
    private static File file;

    synchronized static void open(Socket socket) {
        file = new File(("log_" + socket.getInetAddress() + " " + socket.getPort() + ".txt").replace("/",""));
    }

    synchronized static List<String> getMsgs() {
        List<String> list = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String str;
            int i = 0;
            while (((str = reader.readLine()) != null) && i < 100) {
                list.add(str);
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    synchronized static void putLog(String msg) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(msg + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
