package ru.GeekBrains.Lesson6HomeWork.server;

public interface AuthService {
//    Описывает сервис аутентификации клиента на стороне сервера
    void start();
    void stop();
    String getNickByLoginAndPass(String login, String pass);
    void setNick(String login, String nick);
    boolean LoginIsBusy(String login);
    void addNewUser(String login, String pass, String nick);
}
