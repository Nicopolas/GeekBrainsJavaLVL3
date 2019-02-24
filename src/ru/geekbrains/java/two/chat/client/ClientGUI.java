package ru.geekbrains.java.two.chat.client;

import ru.geekbrains.java.two.library.Messages;
import ru.geekbrains.java.two.network.SocketThread;
import ru.geekbrains.java.two.network.SocketThreadListener;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class ClientGUI extends JFrame implements ActionListener, Thread.UncaughtExceptionHandler, SocketThreadListener {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientGUI();
            }
        });
    }

    private final int WIDTH = 400;
    private final int HEIGHT = 300;
    private final String TITLE = "Chat Client";
    private final String[] EMPTY = new String[0];

    private final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss: ");
    private final JTextArea log = new JTextArea();
    private final JPanel panelTop = new JPanel(new GridLayout(2, 3));
    private final JTextField tfIPAddress = new JTextField("127.0.0.1");
    private final JTextField tfPort = new JTextField("8189");
    private final JCheckBox cbAlwaysOnTop = new JCheckBox("Always on top");
    private final JTextField tfLogin = new JTextField("ivan_igorevich");
    private final JPasswordField tfPassword = new JPasswordField("12345678");
    private final JButton btnLogin = new JButton("Login");
    private final JPanel panelBottom = new JPanel(new BorderLayout());
    private final JButton btnDisconnect = new JButton("Disconnect");
    private final JTextField tfMessage = new JTextField();
    private final JButton btnSend = new JButton("Send");
    private final JList<String> userList = new JList<>();

    private boolean shownIoErrors = false;
    private SocketThread socketThread;

    private ClientGUI() {
        Thread.setDefaultUncaughtExceptionHandler(this);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(WIDTH, HEIGHT);
        setTitle(TITLE);

        panelTop.add(tfIPAddress);
        panelTop.add(tfPort);
        panelTop.add(cbAlwaysOnTop);
        panelTop.add(tfLogin);
        panelTop.add(tfPassword);
        panelTop.add(btnLogin);

        panelBottom.add(btnDisconnect, BorderLayout.WEST);
        panelBottom.add(tfMessage, BorderLayout.CENTER);
        panelBottom.add(btnSend, BorderLayout.EAST);
        panelBottom.setVisible(false);

        log.setEditable(false);
        JScrollPane scrollLog = new JScrollPane(log);

        JScrollPane scrollUsers = new JScrollPane(userList);
        scrollUsers.setPreferredSize(new Dimension(100, 0));

        cbAlwaysOnTop.addActionListener(this);
        btnSend.addActionListener(this);
        tfMessage.addActionListener(this);
        btnLogin.addActionListener(this);
        btnDisconnect.addActionListener(this);
        userList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {

            }
        });


        add(scrollUsers, BorderLayout.EAST);
        add(panelBottom, BorderLayout.SOUTH);
        add(panelTop, BorderLayout.NORTH);
        add(scrollLog, BorderLayout.CENTER);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == cbAlwaysOnTop) {
            setAlwaysOnTop(cbAlwaysOnTop.isSelected());
        } else if (src == btnSend || src == tfMessage) {
            sendMessage();
        } else if (src == btnLogin || src == tfLogin || src == tfPassword || src == tfIPAddress || src == tfPort) {
            connect();
        } else if (src == btnDisconnect) {
            socketThread.close();
        } else {
            throw new RuntimeException("Unknown source: " + src);
        }
    }

    void sendMessage() {
        String msg = tfMessage.getText();
        String username = tfLogin.getText();
        if ("".equals(msg)) return;
        tfMessage.setText(null);
        tfMessage.requestFocusInWindow();
        socketThread.sendMessage(Messages.getTypeClientBcast(msg));
//        try (FileWriter out = new FileWriter("log.txt", true)) {
//            out.write(username + ": " + msg + "\n");
//            out.flush();
//        } catch (IOException e) {
//            if (!shownIoErrors) {
//                shownIoErrors = true;
//                JOptionPane.showMessageDialog(this, "File write error", "Exception", JOptionPane.ERROR_MESSAGE);
//            }
//        }
    }

    private void putLog(String msg) {
        if ("".equals(msg)) return;
        HistoryLogHelper.putLog(msg);
        putLogArea(msg);
    }

    private void putLogArea(String msg) {
        if ("".equals(msg)) return;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(msg + "\n");
                log.setCaretPosition(log.getDocument().getLength());
            }
        });
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        e.printStackTrace();
        StackTraceElement[] ste = e.getStackTrace();
        String msg;
        if (ste.length == 0)
            msg = "Empty stacktrace";
        else
            msg = e.getClass().getCanonicalName() + ": " + e.getMessage() + "\n" + "\t at " + ste[0];

        JOptionPane.showMessageDialog(this, msg, "Exception", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }

    private void connect() {
        Socket socket = null;
        try {
            socket = new Socket(tfIPAddress.getText(), Integer.parseInt(tfPort.getText()));
            HistoryLogHelper.open(socket);
        } catch (IOException e) {
            log.append("Exception: " + e.getMessage());
        }
        socketThread = new SocketThread(this, "Client thread", socket);
    }

    private void handleMessage(String value) {
        String[] arr = value.split(Messages.DELIMITER);
        String msgType = arr[0];
        switch (msgType) {
            case Messages.AUTH_ACCEPT:
                setTitle(TITLE + " entered under nickname: " + arr[1]);
                break;
            case Messages.AUTH_ERROR:
                putLog(value);
                break;
            case Messages.MSG_FORMAT_ERROR:
                putLog(value);
                socketThread.close();
                break;
            case Messages.TYPE_BROADCAST:
                putLog(dateFormat.format(Long.parseLong(arr[1])) +
                        arr[2] + ": " + arr[3]);
                break;
            case Messages.USER_LIST:
                String users = value.substring(Messages.USER_LIST.length() +
                        Messages.DELIMITER.length());
                String[] userArray = users.split(Messages.DELIMITER);
                Arrays.sort(userArray);
                userList.setListData(userArray);
                break;
            default:
                throw new RuntimeException("Can't recognize message from server: " + value);
        }

    }

    /**
     * SocketThreadListener methods
     */

    @Override
    public void onStartSocketThread(SocketThread thread, Socket socket) {
        log.setText("");
        putLog("Connection established");
        for (String msg : HistoryLogHelper.getMsgs()) {
            putLogArea(msg);
        }
        panelBottom.setVisible(true);
        panelTop.setVisible(false);
    }

    @Override
    public void onStopSocketThread(SocketThread thread) {
        putLog("Connection lost");
        userList.setListData(EMPTY);
        setTitle(TITLE);
        panelBottom.setVisible(false);
        panelTop.setVisible(true);
    }

    @Override
    public void onSocketThreadIsReady(SocketThread thread, Socket socket) {
        String login = tfLogin.getText();
        String password = new String(tfPassword.getPassword());
        thread.sendMessage(Messages.getAuthRequest(login, password));
    }

    @Override
    public void onReceiveString(SocketThread thread, Socket socket, String value) {
        handleMessage(value);
    }

    @Override
    public void onSocketThreadException(SocketThread thread, Exception e) {
//        putLog("exception in socket: " + e.getClass().getName() + ": " + e.getMessage());
    }
}
