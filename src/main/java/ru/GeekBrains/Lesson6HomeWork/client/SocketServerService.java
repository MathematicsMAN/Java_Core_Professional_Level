package ru.GeekBrains.Lesson6HomeWork.client;

import com.google.gson.Gson;
import ru.GeekBrains.Lesson6HomeWork.server.AuthMessage;
import ru.GeekBrains.Lesson6HomeWork.server.Message;
import ru.GeekBrains.Lesson6HomeWork.server.MyServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SocketServerService implements ServerService {
    private Socket socket;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    private boolean isConnected = false;
    private LogHistory logHistory;

    public boolean isConnected() {
        return isConnected;
    }

    @Override
    public boolean openConnection() {
        try {
            socket = new Socket("localhost", MyServer.PORT);
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public LogHistory getLogHistory() {
        return logHistory;
    }

    public AuthMessage authorization(AuthMessage authMessage) throws IOException {
        dataOutputStream.writeUTF(new Gson().toJson(authMessage));

        AuthMessage authMessageRead = new Gson().fromJson(dataInputStream.readUTF(),AuthMessage.class);
        if (isConnected = authMessageRead.isAuthenticated()) {
            this.logHistory = new LogHistory(authMessageRead.getLogin());
        }
        return authMessageRead;
    }

    @Override
    public void closeConnection() {
        try {
            dataOutputStream.close();
            dataInputStream.close();
            socket.close();
            if (logHistory != null) {
                logHistory.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessage(String message) {
        Message msg = new Message();
        msg.setMessage(message);
        try {
            dataOutputStream.writeUTF(new Gson().toJson(msg));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Message readMessages() {
        try {
            return new Gson().fromJson(dataInputStream.readUTF(),Message.class);
        } catch (IOException e) {
            e.printStackTrace();
            return new Message();
        }
    }
}
