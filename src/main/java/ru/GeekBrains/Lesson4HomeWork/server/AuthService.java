package ru.GeekBrains.Lesson4HomeWork.server;

public interface AuthService {
//    Описывает сервис аутентификации клиента на стороне сервера
    void start();
    void stop();
    String getNickByLoginAndPass(String login, String pass);
    void setNick(String login, String nick);

}
