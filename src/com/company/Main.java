package com.company;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
1. Создать три потока, каждый из которых выводит определенную букву (A, B и C) 5 раз (порядок – ABСABСABС). Используйте wait/notify/notifyAll.
2. На серверной стороне сетевого чата реализовать управление потоками через ExecutorService.
*/
public class Main {
    static LetterTerminal letterTerminalA = null;
    static LetterTerminal letterTerminalB = null;
    static LetterTerminal letterTerminalC = null;
    static Object lock = new Object();

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        letterTerminalA = new LetterTerminal("A", letterTerminalB);
        letterTerminalB = new LetterTerminal("B", letterTerminalC);
        letterTerminalC = new LetterTerminal("C", letterTerminalA);

/*        new Thread(letterTerminalA).start();
        new Thread(letterTerminalB).start();
        new Thread(letterTerminalC).start();
        letterTerminalA.notifyTerminal();*//*


        Thread letterTerminalAThread = new Thread(letterTerminalA);
        Thread letterTerminalBThread = new Thread(letterTerminalA);
        Thread letterTerminalCThread = new Thread(letterTerminalA);

        try {
            letterTerminalAThread.join();
            letterTerminalA.notifyTerminal();
            letterTerminalB.notifyTerminal();
            letterTerminalC.notifyTerminal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


*/

        executorService.execute(letterTerminalA);
        executorService.execute(letterTerminalB);
        executorService.execute(letterTerminalC);
//        letterTerminalA.notifyTerminal();
//        letterTerminalB.notifyTerminal();
//        letterTerminalC.notifyTerminal();
        executorService.shutdown();


    }

    static class LetterTerminal implements Runnable {
        String str;
        LetterTerminal nextTerminal;
        int iteration = 0;

        public LetterTerminal(String str, LetterTerminal nextTerminal) {
            this.str = str;
            this.nextTerminal = nextTerminal;
        }

        @Override
        public void run() {
            while (iteration != 5) {
                try {
                    synchronized (lock) {
                        while (StringEnum.get() != str) {
                            lock.wait();
                        }
                        System.out.print(str);
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

    static class StringEnum {
        static int position = 0;
        static String[] strings = {"A", "B", "C"};

        static void next() {
            position = (position + 1) % 3;
        }

        static String get() {
            return strings[position];
        }
    }
}
