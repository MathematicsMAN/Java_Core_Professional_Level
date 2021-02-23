package ru.GeekBrains.Lesson6HomeWork.client;

import ru.GeekBrains.Lesson6HomeWork.server.AuthMessage;
import  ru.GeekBrains.Lesson6HomeWork.server.Message;

import java.io.IOException;

public interface ServerService {
    boolean isConnected();
    boolean openConnection();
    void closeConnection();
    void sendMessage(String message);
    Message readMessages();
//    String authorization(String login, String password) throws IOException;
    AuthMessage authorization(AuthMessage authMessage) throws IOException;
    LogHistory getLogHistory();
}
