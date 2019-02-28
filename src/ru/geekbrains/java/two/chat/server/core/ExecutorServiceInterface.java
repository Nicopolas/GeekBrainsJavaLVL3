package ru.geekbrains.java.two.chat.server.core;


public interface ExecutorServiceInterface {
    Object lock = new Object();

    class StringEnum {
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
