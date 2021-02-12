package ru.GeekBrains.Lesson3HomeWork.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BaseAuthService implements AuthService {
//    Реализует интерфейс AuthService, работающий на основе списка,
//    по которому будет происходить авторизация пользователей в чате
    private final String NAME_OF_SCHEMA = "chat_users";
    private final String URL_TO_DB = "jdbc:mysql://localhost:3306/" + NAME_OF_SCHEMA +
                                     "?characterEncoding=utf8&useUnicode=true&serverTimezone=UTC";
    private List<Entry> entries;
    private boolean canStart = false;

    public BaseAuthService() {
        entries = new ArrayList<>();
        entries.add(new Entry("ivan", "password", "Neivanov"));
        entries.add(new Entry("sharik", "gav", "Auf"));
        entries.add(new Entry("otvertka", "shurup", "KrucuVerchu"));

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
            PreparedStatement preparedStatement = conn.prepareStatement(
                    "insert into " + NAME_OF_SCHEMA +" (Login, Password, Nick) values (?, ?, ?)");
            conn.setAutoCommit(false);
            for (Entry entry : entries) {
                Statement loginIsPresent = conn.createStatement();
                ResultSet resultSet = loginIsPresent.executeQuery("select * from " + NAME_OF_SCHEMA + " where Login = \"" + entry.login + "\"");
                if (!resultSet.next()){
                    preparedStatement.setString(1,entry.login);
                    preparedStatement.setString(2,entry.password);
                    preparedStatement.setString(3,entry.nick);
                    preparedStatement.executeUpdate();
                }
            }
            conn.commit();
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    @Override
    public void start() {
        if (canStart) {
            System.out.println("Сервис авторизации запущен");
        } else {
            System.out.println("Соединение с севером БД отсутствует. Сервис авторизации не запущен!");
        }
    }

    @Override
    public void stop() {
        System.out.println("Сервис авторизации остановлен");
    }

    @Override
    public String getNickByLoginAndPass(String login, String pass) {
        try (Connection conn = DriverManager.getConnection(URL_TO_DB,"root","root")){
            Statement statement = conn.createStatement();

            ResultSet resultSet = statement.executeQuery(
                    "select * from " + NAME_OF_SCHEMA + " where Login = \"" + login + "\" and Password = \"" + pass + "\"");
            if (resultSet.next()){
                return resultSet.getString("Nick");
            } else {
                return null;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    @Override
    public void setNick(String login, String nick) {
        try(Connection conn = DriverManager.getConnection(URL_TO_DB,"root","root")) {
            Statement statement = conn.createStatement();
            statement.executeUpdate("UPDATE " + NAME_OF_SCHEMA +
                    " SET Nick = \"" + nick + "\" WHERE Login = \"" + login + "\"");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
