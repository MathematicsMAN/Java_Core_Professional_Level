package ru.GeekBrains.Lesson6HomeWork.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerApp {
    //стартует приложение
    private static final Logger LOG = LogManager.getLogger(ServerApp.class);

    public static void main(String[] args) {
        LOG.info("Запуск сервера");
        new MyServer();
    }
}
