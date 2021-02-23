package ru.GeekBrains.Lesson6HomeWork.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MyServer {
//    предоставляет работу сервера
    private static final Logger LOG = LogManager.getLogger(MyServer.class);

    public static final int PORT = 8081;
    private List<ClientHandler> clients;
    private AuthService authService;

    public MyServer() {
        try(ServerSocket serverSocket = new ServerSocket(PORT)){
            authService = new BaseAuthService();
            authService.start();
            clients = new ArrayList<>();
            while(true){
                System.out.println("Ожидание подключения клиентов");
                LOG.info("Ожидание подключения клиентов");
                Socket socket = serverSocket.accept();
                System.out.println("Клиент подключился");
                LOG.info("Клиент подключился");
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error("Сокет не подключился");
        } finally {
            if (authService != null) {
                authService.stop();
                LOG.info("Отключение сервера");
            }
        }
    }
//формирование списка подключенных клиентов
    public synchronized void broadcastClientsList(){
        StringBuilder sb = new StringBuilder("/clients: ");
        for (ClientHandler client : clients) {
            sb.append(client.getNick()).append(" ");
        }
        Message message = new Message();
        message.setMessage(sb.toString());
        LOG.info("Рассылка списка пользователей");
        broadcastMessage(message);
    }
//отправка конкретному клиенту сообщение
    public synchronized void sendMsgToClient(ClientHandler from, String nickTo, String msg){
        System.out.printf("Отправка личного сообщения от %s, кому %s: %s\n",
                from.getNick() == null ? "Сервер" : from.getNick(),nickTo,msg);
        LOG.info("Отправка личного сообщения от {}, кому {}: {}",
                from.getNick() == null ? "Сервер" : from.getNick(),nickTo,msg);
        for (ClientHandler client : clients) {
            if (client.getNick().equals(nickTo)){
                Message message = new Message();
                message.setNick(from.getNick());
                message.setMessage(msg);
                client.sendMessage(message);
                return;
            }
        }
        System.out.printf("Сообщение не отправилось. Клиент с ником %s  не подключен к чату\n", nickTo);
        LOG.warn("Сообщение не отправилось. Клиент с ником {}  не подключен к чату", nickTo);
        Message message = new Message();
        message.setMessage("Клиент с этим ником не подключен к чату");
        from.sendMessage(message);
    }

    public synchronized void broadcastMessage(Message message){
        LOG.info("Рассылка всем подключенным пользователям сообщения");
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public synchronized boolean isNickBusy(String nick){
        LOG.info("Проверка на то, что пользователь с ником " + nick + " сейчас в сети");
        for (ClientHandler client : clients) {
            if (nick.equals(client.getNick())){
                return true;
            }
        }
        return false;
    }

    public AuthService getAuthService() {
        return authService;
    }

    public synchronized void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
        broadcastClientsList();
        LOG.info("Добавление пользователя в список online пользователей");
    }

    public synchronized void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        broadcastClientsList();
        LOG.info("Удаление пользователя из списка online пользователей");
    }

    public void updateNick(ClientHandler clientHandler, String newNick){
        for (ClientHandler client : clients) {
            if (client.getLogin().equals(clientHandler.getLogin())){
                client.setNick(newNick);
                return;
            }
        }
    }
}
