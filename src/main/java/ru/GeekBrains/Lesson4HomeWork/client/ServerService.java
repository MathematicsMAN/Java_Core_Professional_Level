package ru.GeekBrains.Lesson4HomeWork.client;

import ru.GeekBrains.Lesson4HomeWork.server.Message;

import java.io.IOException;

public interface ServerService {
    boolean isConnected();
    void openConnection();
    void closeConnection();
    void sendMessage(String message);
    Message readMessages();
    String authorization(String login, String password) throws IOException;
    LogHistory getLogHistory();
}
