package ru.GeekBrains.Lesson6HomeWork.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BaseAuthService implements AuthService {
//    Реализует интерфейс AuthService, работающий на основе списка,
//    по которому будет происходить авторизация пользователей в чате
    private static final Logger LOG = LogManager.getLogger(BaseAuthService.class);


    private final String NAME_OF_SCHEMA = "chat_users";
    private final String URL_TO_DB = "jdbc:mysql://localhost:3306/" + NAME_OF_SCHEMA +
                                     "?characterEncoding=utf8&useUnicode=true&serverTimezone=UTC";
    private List<Entry> entries;
    private boolean canStart = false;
    private boolean addNewUser = false;

    public BaseAuthService() {
        entries = new ArrayList<>();
        canStart = createDB();
    }

    private class Entry {
        private String login;
        private String password;
        private String nick;

        public Entry(String login, String password, String nick) {
            this.login = login;
            this.password = password;
            this.nick = nick;
        }
    }

    private boolean createDB() {
        try(Connection conn = DriverManager.getConnection(URL_TO_DB,"root","root")){
            LOG.info("К БД MySQL подключение прошло успешно");
            Statement statement = conn.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS " + NAME_OF_SCHEMA +" (\n" +
                    "\tUserID INT(10) auto_increment NOT NULL,\n" +
                    "\tLogin VARCHAR(40) NOT NULL COLLATE utf8_unicode_ci,\n" +
                    "\tPassword VARCHAR(40) NOT NULL COLLATE utf8_unicode_ci,\n" +
                    "\tNick VARCHAR(40) NOT NULL COLLATE utf8_unicode_ci,\n" +
                    "\tPRIMARY KEY (UserID) USING BTREE\n" +
                    ")\n" +
                    "COLLATE=utf8_unicode_ci\n" +
                    "ENGINE=InnoDB\n" +
                    ";\n");
            LOG.info("Схема " + NAME_OF_SCHEMA + " создана (если её не было)");
            PreparedStatement preparedStatement = conn.prepareStatement(
                    "INSERT INTO " + NAME_OF_SCHEMA +" (Login, Password, Nick) VALUES (?, ?, ?)");
            conn.setAutoCommit(false);
            for (Entry entry : entries) {
                Statement loginIsPresent = conn.createStatement();
                ResultSet resultSet = loginIsPresent.executeQuery("SELECT * FROM " + NAME_OF_SCHEMA + " WHERE Login = \"" + entry.login + "\"");
                if (!resultSet.next()){
                    preparedStatement.setString(1,entry.login);
                    preparedStatement.setString(2,entry.password);
                    preparedStatement.setString(3,entry.nick);
                    preparedStatement.executeUpdate();
                    LOG.info("Пользователь с логином " + entry.login + " добавлен");
                }
            }
            conn.commit();
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            LOG.error("Ошибка подключения к БД или чтения из БД: ", throwables);
            return false;
        } finally {
            LOG.info("Отключение от БД");
        }
    }

    @Override
    public void start() {
        if (canStart) {
            System.out.println("Сервис авторизации запущен");
            LOG.info("Сервис авторизации запущен");
        } else {
            System.out.println("Соединение с севером БД отсутствует. Сервис авторизации не запущен!");
            LOG.error("Соединение с севером БД отсутствует. Сервис авторизации не запущен!");
        }
    }

    @Override
    public void stop() {
        System.out.println("Сервис авторизации остановлен");
        LOG.info("Сервис авторизации остановлен");
    }

    @Override
    public String getNickByLoginAndPass(String login, String pass) {
        try (Connection conn = DriverManager.getConnection(URL_TO_DB,"root","root")){
            LOG.info("К БД подключились (получение ника по логину и паролю)");
            Statement statement = conn.createStatement();

            ResultSet resultSet = statement.executeQuery(
                    "SELECT * FROM " + NAME_OF_SCHEMA + " WHERE Login = \"" + login + "\" AND Password = \"" + pass + "\"");
            if (resultSet.next()){
                LOG.info("По логину " + login + " и паролю ник получен");
                return resultSet.getString("Nick");
            } else {
                LOG.info("По логину " + login + " и паролю ник не получен");
                return null;
            }
        } catch (SQLException throwables) {
            LOG.error("Ошибка подключения к БД или чтения из БД: ", throwables);
            throwables.printStackTrace();
        } finally {
            LOG.info("Отключение от БД");
        }
        return null;
    }

    @Override
    public void setNick(String login, String nick) {
        try(Connection conn = DriverManager.getConnection(URL_TO_DB,"root","root")) {
            LOG.info("К БД подключились (изменение ника пользователя)");
            Statement statement = conn.createStatement();
            statement.executeUpdate("UPDATE " + NAME_OF_SCHEMA +
                    " SET Nick = \"" + nick + "\" WHERE Login = \"" + login + "\"");
            LOG.info("Пользователь под логином " + login + " изменил ник на " + nick);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            LOG.error("Ошибка подключения к БД или записи в БД: ", throwables);
        } finally {
            LOG.info("Отключение от БД");
        }
    }

    public boolean LoginIsBusy(String login) {
        try(Connection conn = DriverManager.getConnection(URL_TO_DB,"root","root")) {
            LOG.info("К БД подключились (проверка существования логина)");
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(
                    "SELECT Login FROM " + NAME_OF_SCHEMA + " WHERE Login = \"" + login + "\"");
            return resultSet.next();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            LOG.error("Ошибка подключения к БД или чтения из БД: ", throwables);
        } finally {
            LOG.info("Отключение от БД");
        }
        return false;
    }

    @Override
    public void addNewUser(String login, String pass, String nick) {
        try(Connection conn = DriverManager.getConnection(URL_TO_DB,"root","root")) {
            LOG.info("К БД подключились (добавление нового пользователя)");
            PreparedStatement preparedStatement = conn.prepareStatement(
                    "INSERT INTO " + NAME_OF_SCHEMA +" (Login, Password, Nick) VALUES (?, ?, ?)");
            conn.setAutoCommit(false);
            Statement loginIsPresent = conn.createStatement();
            ResultSet resultSet = loginIsPresent.executeQuery("SELECT * FROM " + NAME_OF_SCHEMA + " WHERE Login = \"" + login + "\"");
            if (!resultSet.next()){
                preparedStatement.setString(1,login);
                preparedStatement.setString(2,pass);
                preparedStatement.setString(3,nick);
                preparedStatement.executeUpdate();
                LOG.info("Пользователь с логином " + login + " добавлен");
                entries.add(new Entry(login,pass,nick));
            }
            conn.commit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            LOG.error("Ошибка подключения к БД или записи в БД: ", throwables);
        } finally {
            LOG.info("Отключение от БД");
        }
    }
}
