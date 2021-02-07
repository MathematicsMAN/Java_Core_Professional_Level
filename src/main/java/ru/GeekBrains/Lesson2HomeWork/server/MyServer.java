package ru.GeekBrains.Lesson2HomeWork.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MyServer {
//    предоставляет работу сервера
//    private static final int PORT = 8081;
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
                Socket socket = serverSocket.accept();
                System.out.println("Клиент подключился");
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (authService != null) {
                authService.stop();
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
        broadcastMessage(message);
    }
//отправка конкретному клиенту сообщение
    public synchronized void sendMsgToClient(ClientHandler from, String nickTo, String msg){
        for (ClientHandler client : clients) {
            if (client.getNick().equals(nickTo)){
                System.out.printf("Отправляем личное сообщение от %s, кому %s: %s\n",
                        from.getNick() == null ? "Сервер" : from.getNick(),nickTo,msg);
                Message message = new Message();
                message.setNick(from.getNick());
                message.setMessage(msg);
                client.sendMessage(message);
                return;
            }
        }
        System.out.printf("Клиент с ником %s  не подключен к чату\n", nickTo);
        Message message = new Message();
        message.setMessage("Клиент с этим ником не подключен к чату");
        from.sendMessage(message);
    }

    public synchronized void broadcastMessage(Message message){
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public synchronized boolean isNickBusy(String nick){
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
    }

    public synchronized void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        broadcastClientsList();
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
