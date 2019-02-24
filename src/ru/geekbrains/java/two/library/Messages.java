package ru.geekbrains.java.two.library;

public abstract class Messages {
    public static final String DELIMITER = "§";
    public static final String AUTH_REQUEST = "/auth_request";
    public static final String AUTH_ACCEPT = "/auth_accept";
    public static final String AUTH_ERROR = "/auth_error";
    public static final String TYPE_BROADCAST = "/bcast";
    public static final String TYPE_CLIENT_BCAST = "/client_bcast";
    public static final String MSG_FORMAT_ERROR = "/msg_format_error";
    public static final String USER_LIST = "/user_list";

    public static String getAuthRequest(String login, String password) {
        // /auth_request§login§password
        return AUTH_REQUEST + DELIMITER + login + DELIMITER + password;
    }

    public static String getAuthAccept(String nickname) {
        return AUTH_ACCEPT + DELIMITER + nickname;
    }

    public static String getAuthError() {
        return AUTH_ERROR;
    }

    public static String getTypeBroadcast(String src, String msg) {
        return TYPE_BROADCAST + DELIMITER + System.currentTimeMillis() +
                DELIMITER + src + DELIMITER + msg;
    }

    public static String getMsgFormatError(String msg) {
        return MSG_FORMAT_ERROR + DELIMITER + msg;
    }

    public static String getUserList(String userList) {
        return  USER_LIST + DELIMITER + userList;
    }

    public static String getTypeClientBcast(String msg) {
        return TYPE_CLIENT_BCAST + DELIMITER + msg;
    }
}

/*
* {
*   'type':'....',
*   'login':'...',
*   'password':'...',
*   'timestamp':'...'
* }
* */