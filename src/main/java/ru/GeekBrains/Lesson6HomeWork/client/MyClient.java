package ru.GeekBrains.Lesson6HomeWork.client;

import ru.GeekBrains.Lesson6HomeWork.server.AuthMessage;
import ru.GeekBrains.Lesson6HomeWork.server.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MyClient extends JFrame {
    private final long SESSION_TIME = TimeUnit.SECONDS.toMillis(120);
    private final int ROWS_IN_MAIN_CHAT = 5;
    private final int COLUMNS_IN_MAIN_CHAT = 10;
    private final ServerService serverService;
    private Thread checkReadMessage = null;
    private LogHistory logHistory;
    private final JList<String> jListOnlineUsers = new JList< >();

    public MyClient() {
        super("Чат");

        serverService = new SocketServerService();
        if(!serverService.openConnection()){
            // Вывести сообщение об отсутствии связи с сервером
            System.out.println("Отсутствует связь с сервером");
//            System.exit(0);
            return;
        }

        JLabel authLabel = new JLabel();
        JLabel timeLabel = new JLabel();
        JPanel jPanelMain = new JPanel();
        JTextArea mainChat = new JTextArea(ROWS_IN_MAIN_CHAT,COLUMNS_IN_MAIN_CHAT);

        ExecutorService executorService = Executors.newSingleThreadExecutor();

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
                timeLabel.setText("Осталось " + ((SESSION_TIME - (timeNow - lastActiveTime)) / 1000)
                        + " секунд до успешной авторизации");
            }
            jPanelMain.setVisible(true);
            readMessageThread(mainChat, authLabel, executorService);
        }).start();

        setLayout(new BoxLayout(getContentPane(),BoxLayout.Y_AXIS));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(500,200,500,300);
//         сделать размер окна неизменяемым

        jPanelMain.setLayout(new BorderLayout());
        jPanelMain.setSize(300,50);
        jPanelMain.setVisible(false);

        mainChat.setSize(400,250);

        initLoginPanel(mainChat, authLabel, timeLabel);

        JPanel jPanelNick = new JPanel();
        jPanelNick.setLayout(new BoxLayout(jPanelNick, BoxLayout.X_AXIS));
        JTextField jTextFieldNewNick = new JTextField("New_Nick");
        JButton jButtonChangeNick = new JButton("Change Nick");
        jPanelNick.add(authLabel);
        jPanelNick.add(jTextFieldNewNick);
        jPanelNick.add(jButtonChangeNick);

        JLabel jLabelSend = new JLabel("My message: ");

        JTextField myMessage = new JTextField();

        JButton jButtonSend = new JButton("Send");
        final String[] preMessage = {""};
        JPanel jPanelMyMessageSend = new JPanel();
        jPanelMyMessageSend.setLayout(new BoxLayout(jPanelMyMessageSend, BoxLayout.X_AXIS));
        jPanelMyMessageSend.add(jLabelSend);
        jPanelMyMessageSend.add(myMessage);
        jPanelMyMessageSend.add(jButtonSend);

        jButtonChangeNick.addActionListener(e ->
            changeNick(jTextFieldNewNick)
        );
        jTextFieldNewNick.addActionListener(e ->
            changeNick(jTextFieldNewNick)
        );

        jButtonSend.addActionListener(actionEvent -> {
            myMessage.setText(preMessage[0] + myMessage.getText());
            sendMessage(myMessage);
        });

        myMessage.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER){
                    myMessage.setText(preMessage[0] + myMessage.getText());
                    sendMessage(myMessage);
                }
            }
        });

        jListOnlineUsers.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
//                super.mouseClicked(e);
                if (e.getClickCount() == 2){
                    if (jListOnlineUsers.getSelectedIndex() == 0) {
                        jLabelSend.setText("My message: ");
                        preMessage[0] = "";
                    } else {
                        jLabelSend.setText("Send to " + jListOnlineUsers.getSelectedValue());
                        preMessage[0] = "/w " + jListOnlineUsers.getSelectedValue() + " ";
                    }
//                    myMessage.setText(preMessage[0]);
                }
            }
        });

        jPanelMain.add(BorderLayout.NORTH,jPanelNick);
        jPanelMain.add(BorderLayout.CENTER,mainChat);
        jPanelMain.add(BorderLayout.SOUTH,jPanelMyMessageSend);
        jPanelMain.add(BorderLayout.EAST,jListOnlineUsers);
        add(jPanelMain);
    }

    private void initLoginPanel(JTextArea mainChat, JLabel authLabel, JLabel timeLabel) {
        JPanel jPanelAuthorization = new JPanel();
        jPanelAuthorization.setLayout(new BoxLayout(jPanelAuthorization, BoxLayout.Y_AXIS));

        JPanel jPanelLabelsAndFields = new JPanel();
        jPanelLabelsAndFields.setLayout(new GridLayout(4,2));

        JLabel jLabelLogin = new JLabel();
        jLabelLogin.setText("Логин: ");
        jPanelLabelsAndFields.add(jLabelLogin);
        JTextField loginField = new JTextField();
        loginField.setToolTipText("Логин");
        jPanelLabelsAndFields.add(loginField);

        JLabel jLabelPassword = new JLabel();
        jLabelPassword.setText("Пароль: ");
        jPanelLabelsAndFields.add(jLabelPassword);
        JPasswordField jFieldPassword = new JPasswordField();
        jFieldPassword.setToolTipText("Пароль");
        jPanelLabelsAndFields.add(jFieldPassword);

        JLabel jLabelSecondPassword = new JLabel();
        jLabelSecondPassword.setText("Пароль ещё раз:");
        jPanelLabelsAndFields.add(jLabelSecondPassword);
        JPasswordField jFieldSecondPassword = new JPasswordField();
        jFieldSecondPassword.setToolTipText("Пароль ещё раз: ");
        jFieldSecondPassword.setEnabled(false);
        jPanelLabelsAndFields.add(jFieldSecondPassword);

        JLabel jLabelNick = new JLabel();
        jLabelNick.setText("Ник:  ");
        jPanelLabelsAndFields.add(jLabelNick);
        JTextField loginNick = new JTextField();
        loginNick.setToolTipText("Ник");
        loginNick.setEnabled(false);
        jPanelLabelsAndFields.add(loginNick);

        JCheckBox jCheckBoxNewUser = new JCheckBox();
        jCheckBoxNewUser.setText("Войти под новым пользователем");
        jCheckBoxNewUser.setSelected(false);
        JButton authButton = new JButton("Авторизоваться");

        jPanelAuthorization.add(jPanelLabelsAndFields);
        jPanelAuthorization.add(jCheckBoxNewUser);
        jPanelAuthorization.add(authButton);
        jPanelAuthorization.add(timeLabel);
        add(jPanelAuthorization);

        authButton.addActionListener(actionEvent ->
                checkAuth(mainChat, authLabel, jPanelAuthorization, loginField, jFieldPassword, jFieldSecondPassword, jCheckBoxNewUser, loginNick)
        );
        loginField.addActionListener(e ->
                checkAuth(mainChat, authLabel, jPanelAuthorization, loginField, jFieldPassword, jFieldSecondPassword, jCheckBoxNewUser, loginNick)
        );
        jFieldPassword.addActionListener(e ->
                checkAuth(mainChat, authLabel, jPanelAuthorization, loginField, jFieldPassword, jFieldSecondPassword, jCheckBoxNewUser, loginNick)
        );
        jFieldSecondPassword.addActionListener(e ->
                checkAuth(mainChat, authLabel, jPanelAuthorization, loginField, jFieldPassword, jFieldSecondPassword, jCheckBoxNewUser, loginNick)
        );
        loginNick.addActionListener(e ->
                checkAuth(mainChat, authLabel, jPanelAuthorization, loginField, jFieldPassword, jFieldSecondPassword, jCheckBoxNewUser, loginNick)
        );

        jCheckBoxNewUser.addActionListener(e -> {
            loginNick.setEnabled(jCheckBoxNewUser.isSelected());
            jFieldSecondPassword.setEnabled(jCheckBoxNewUser.isSelected());
        });
    }

    private void checkAuth(JTextArea mainChat, JLabel authLabel, JPanel jPanelAuthorization, JTextField loginField,
                           JPasswordField jFieldPassword, JPasswordField jFieldSecondPassword, JCheckBox jCheckBoxNewUser, JTextField loginNick) {
        String lgn = loginField.getText();
        String psw = new String(jFieldPassword.getPassword());
        AuthMessage authMessage = new AuthMessage();
        authMessage.setNewUser(jCheckBoxNewUser.isSelected());

        if (lgn == null || lgn.isEmpty()){
            loginField.setText("Логин и не может быть пустым");
            return;
        }
        if (psw == null || psw.isEmpty()){
            loginField.setText("Пароль не может быть пустым");
            return;
        }

        if (jCheckBoxNewUser.isSelected()){
            String pswSecond = new String(jFieldSecondPassword.getPassword());
            if (!psw.equals(pswSecond) || pswSecond.isEmpty()){
                loginField.setText("Пароли не совпадают");
                jFieldPassword.setText("");
                jFieldSecondPassword.setText("");
                return;
            }
            String nick = loginNick.getText();
            if (nick == null || nick.isEmpty()){
                loginNick.setText("Ник не может быть пустым");
                return;
            }
            String[] tokens = nick.split("\\s");
            if (tokens.length > 1){
                loginNick.setText("Ник не должен быть более 1 слова");
                return;
            }
            authMessage.setLogin(lgn);
            authMessage.setPassword(psw);
            authMessage.setNick(nick);
            try {
                authMessage = serverService.authorization(authMessage);
                if (!serverService.isConnected()){
                    loginField.setText(authMessage.getErrorMessage());
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else  {
            try {
                authMessage.setLogin(lgn);
                authMessage.setPassword(psw);
                authMessage = serverService.authorization(authMessage);

                if (!serverService.isConnected()) {
                    loginField.setText(authMessage.getErrorMessage());
                    jFieldPassword.setText("");
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        authLabel.setText("Nick: " + authMessage.getNick() + " ");
        jPanelAuthorization.setVisible(false);
        logHistory = serverService.getLogHistory();
        mainChat.setText(logHistory.readLastLines());
    }

    private void readMessageThread(JTextArea mainChat, JLabel authLabel, ExecutorService executorService) {
//        String[] strings;
        checkReadMessage = new Thread(() -> {
            while (true){
                Message message = serverService.readMessages();
                if (!message.getMessage().startsWith("/")){
                    printToUI(mainChat,message);
                    continue;
                }
                String[] tokens = message.getMessage().split("\\s");
                switch (tokens[0]) {
                    case "/clients:": {
                        tokens[0] = "Отправить: ВСЕМ";
                        jListOnlineUsers.setListData(tokens);
                        continue;
                    }
                    case "/n": {
                        authLabel.setText("Nick: " + tokens[1] + " ");
                        continue;
                    }
                }
            }
        });
        executorService.execute(checkReadMessage);
//                checkReadMessage.start();
        if (!executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    private void sendMessage(JTextField myMessage) {
        serverService.sendMessage(myMessage.getText());
        myMessage.setText("");
    }

    private void changeNick(JTextField jTextFieldNewNick) {
        serverService.sendMessage("/n " + jTextFieldNewNick.getText());
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
