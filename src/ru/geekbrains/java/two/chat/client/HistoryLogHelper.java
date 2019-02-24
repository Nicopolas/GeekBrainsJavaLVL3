package ru.geekbrains.java.two.chat.client;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class HistoryLogHelper {
    private static String fileName;
    private static FileWriter fileWriter;
    private static FileReader fileReader;
    private static File file;

    synchronized static void open(Socket socket) {
        fileName = ("log_" + socket.getInetAddress() + " " + socket.getPort() + ".txt").replace("/","");
        file = new File(fileName);
/*        try {
            fileWriter = new FileWriter(fileName);
            fileReader = new FileReader(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    synchronized static List<String> getMsgs() {
        List<String> list = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String str;
            while ((str = reader.readLine()) != null) {
                list.add(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    synchronized static void putLog(String msg) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(msg + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    synchronized static void close() {
/*        try {
            fileReader.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}
