package com.company;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
1. Создать три потока, каждый из которых выводит определенную букву (A, B и C) 5 раз (порядок – ABСABСABС). Используйте wait/notify/notifyAll.
(Без второй части задания)
*/
public class Main {
    static final Object lock = new Object();

    public static void main(String[] args) {

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        for (String str : LetterTerminal.StringEnum.getAll()) {
            executorService.execute(new LetterTerminal(str));
        }
        executorService.shutdown();
    }

    static class LetterTerminal implements Runnable {
        private String str;
        private int iteration = 0;

        public LetterTerminal(String str) {
            this.str = str;
        }

        @Override
        public void run() {
            while (iteration != 5) {
                try {
                    synchronized (lock) {
                        while (!StringEnum.get().equals(str)) {
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

        static class StringEnum {
            private static int position = 0;
            private static String[] strings = {"A", "B", "C"};

            public static void next() {
                position = (position + 1) % 3;
            }

            public static String get() {
                return strings[position];
            }

            public static String[] getAll() {
                return strings;
            }
        }
    }
}
