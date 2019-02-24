package ru.geekbrains.java.two.chat.server.core;

import java.sql.*;

public abstract class SqlClient {
    private static Connection connection;
    private static Statement statement;

    synchronized static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:chatDB.db");
            statement = connection.createStatement();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    synchronized static void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    synchronized static String getNickname(String login, String password) {
        String request = "SELECT nickname FROM users WHERE login='" +
                login + "' AND password='" + password + "'";
        try (ResultSet set = statement.executeQuery(request)) {
            if (set.next()) {
                return set.getString(1);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
