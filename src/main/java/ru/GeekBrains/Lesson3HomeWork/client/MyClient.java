package ru.GeekBrains.Lesson3HomeWork.client;

import ru.GeekBrains.Lesson3HomeWork.server.Message;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MyClient extends JFrame {
    private final long SESSION_TIME = TimeUnit.SECONDS.toMillis(120);
    private final ServerService serverService;
    private Thread checkReadMessage = null;
    LogHistory logHistory;

    public MyClient() {
        super("Чат");

        serverService = new SocketServerService();
        serverService.openConnection();

        JLabel authLabel = new JLabel();
        long lastActiveTime = System.currentTimeMillis();
        Thread finalCheckReadMessage = checkReadMessage;
        new Thread(() -> {
            while (!serverService.isConnected()) {
                long timeNow = System.currentTimeMillis();
                if ((timeNow - lastActiveTime) > SESSION_TIME) {
                    serverService.closeConnection();
                    this.setVisible(false);
                    try {
                        if (finalCheckReadMessage != null) {
                            finalCheckReadMessage.join();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.exit(0);
                }
                authLabel.setText("Осталось " + ((SESSION_TIME - (timeNow - lastActiveTime)) / 1000)
                        + " секунд до успешной авторизации");
            }
        }).start();

        JPanel jPanel = new JPanel();
        setLayout(new BoxLayout(getContentPane(),BoxLayout.Y_AXIS));
        jPanel.setLayout(new BoxLayout(jPanel,BoxLayout.X_AXIS));
        jPanel.setSize(300,50);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(500,200,500,300);

        JTextArea mainChat = new JTextArea();
        mainChat.setSize(400,250);

        initLoginPanel(mainChat, authLabel);

        JTextField myMessage = new JTextField();

        JButton send = new JButton("Send");
//        send.setSize(50,200);
        send.addActionListener(actionEvent -> sendMessage(myMessage));

        myMessage.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER){
                    sendMessage(myMessage);
                }
            }
        });

        add(mainChat);
        jPanel.add(send);
        jPanel.add(myMessage);
        add(jPanel);
    }

    private void initLoginPanel(JTextArea mainChat, JLabel authLabel) {
        JTextField loginField = new JTextField();
        loginField.setText("ivan");
        loginField.setToolTipText("Логин");
        JPasswordField password = new JPasswordField();
        password.setToolTipText("Password");
        password.setText("Пароль");

        JButton authButton = new JButton("Авторизоваться");

        authButton.addActionListener(actionEvent -> {
            String lgn = loginField.getText();
            String psw = new String(password.getPassword());
            if (lgn != null && psw != null && !lgn.isEmpty() && !psw.isEmpty()){
//                logHistory = null;
                try {
                    String nick = serverService.authorization(lgn,psw);
                    if (nick == null){
                        loginField.setText("Неверный логин и/или пароль; или такой логин уже используется");
                        password.setText("");
                        return;
                    }
                    authLabel.setText("On Line, nick " + nick);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                logHistory = serverService.getLogHistory();
                mainChat.setText(logHistory.readLastLines());

                checkReadMessage = new Thread(() -> {
                    while (true){
                        Message message = serverService.readMessages();
                        if (!message.getMessage().startsWith("/")){
                            printToUI(mainChat,message);
//                            logHistory.writeToHistory(message.getMessage());
                            continue;
                        }
                        String[] tokens = message.getMessage().split("\\s");
                        switch (tokens[0]) {
                            case "/clients": {
                                continue;
                            }
                            case "/n": {
                                authLabel.setText("On Line, nick " + tokens[1]);
                                continue;
                            }
                        }

                    }
                });
                checkReadMessage.start();
            }
        });

        add(loginField);
        add(password);
        add(authButton);
        add(authLabel);
    }

    private void sendMessage(JTextField myMessage) {
        serverService.sendMessage(myMessage.getText());
        myMessage.setText("");
    }

    private void printToUI(JTextArea mainChat, Message message){
        Date date = new Date(System.currentTimeMillis());
        String dateToString = String.format("(%1$td %1$tB %1$tY %1$tH:%1$tM:%1$tS) ", date);

        String str = dateToString + (message.getNick() != null ? message.getNick(): "Сервер")
                + " написал:\n" + message.getMessage() + "\n";
        mainChat.append(str);
        logHistory.writeToHistory(str);
    }
}
