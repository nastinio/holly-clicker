package ru.nastinio;

import ru.nastinio.Enums.SQLquery;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataBaseWorker {
    // JDBC driver name and database URL
    private final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    //С настройками часового пояса
    private final String DB_URL = "jdbc:mysql://localhost/holly-clicker-db?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";       //"jdbc:mysql://127.0.0.1:3306/holly-clicker-db";

    private final String DB_USER = "root";
    private final String DB_PASSWORD = "atlanta";

    private Connection connection;
    private PreparedStatement pstmt;
    private Statement stmt;

    DataBaseWorker() {
        try {
            Class.forName(JDBC_DRIVER);     //Загружаем драйвер
        } catch (Exception ex) {
            //выводим наиболее значимые сообщения
            Logger.getLogger(DataBaseWorker.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("-------------------------------");
        }
    }

    //Для работы с таблицей потенциальных друзей
    public void insertUserToPotentialFriendsList(User user) {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);   //Создаём соединение
            String SQL_INSERT = "INSERT INTO `holly-clicker-db`.`potential-friends-list` (`host-profile-link`, " +
                    "`user-id`, `user-profile-link`, `user-name`, `user-birthday`, `user-age`, `user-city`, " +
                    "`date-request`, `was-sent-request-to-friend`, `status-request-answer`, `was-sent-start-msg`, `comment`," +
                    "`count-friends`, `count-common-friends`, `count-followers`)" +
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

            pstmt = connection.prepareStatement(SQL_INSERT);

            pstmt.setString(1, user.getHostProfileLink());

            pstmt.setInt(2, user.getProfileID());
            pstmt.setString(3, user.getProfileLink());
            pstmt.setString(4, user.getPageName());

            //pstmt.setString(5,user.getBirthday());
            pstmt.setString(5,null);
            pstmt.setInt(6, user.getAge());
            pstmt.setString(7, user.getCity());

            pstmt.setString(8, user.getDateRequest());

            pstmt.setBoolean(9, user.wasSentRequestToFriend());
            pstmt.setInt(10, 0);
            pstmt.setInt(11, 0);

            pstmt.setString(12, user.getComment());

            pstmt.setInt(13, user.getCountFriends());
            pstmt.setInt(14, user.getCountCommonFriends());
            pstmt.setInt(15, user.getCountFollowers());

            pstmt.executeUpdate();


        } catch (Exception ex) {
            //выводим наиболее значимые сообщения
            Logger.getLogger(DataBaseWorker.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("-------------------------------");
        } finally {
            closePreparedStatement();
            closeConnection();
        }

    }

    public ArrayList getAllFromPotentialFriendsList() {
        ArrayList<User> listUsers = new ArrayList<>();
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);   //Создаём соединение
            String SQL_SELECT = "SELECT * FROM `holly-clicker-db`.`potential-friends-list`;";
            stmt = connection.createStatement();
            ResultSet resSet = stmt.executeQuery(SQL_SELECT);
            while (resSet.next()) {
                int profileID = resSet.getInt("user-id");
                String profileLink = resSet.getString("user-profile-link");
                String pageName = resSet.getString("user-name");

                User tempUser = new User(profileID, profileLink, pageName);
                listUsers.add(tempUser);
            }

            resSet.close();
        } catch (Exception ex) {
            //выводим наиболее значимые сообщения
            Logger.getLogger(DataBaseWorker.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("-------------------------------");
        } finally {
            closeStatement();
            closeConnection();
        }

        return listUsers;
    }

    public boolean updateStatusRequest(int profileID, int status) {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);   //Создаём соединение
            String SQL_UPDATE = "UPDATE `holly-clicker-db`.`current-request-to-friend-list` SET `status-answer`=? WHERE `user-id`=?";
            pstmt = connection.prepareStatement(SQL_UPDATE);

            pstmt.setInt(1, status);
            pstmt.setInt(2, profileID);

            pstmt.executeUpdate();

        } catch (Exception ex) {
            //выводим наиболее значимые сообщения
            Logger.getLogger(DataBaseWorker.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("-------------------------------");
        } finally {
            closePreparedStatement();
            closeConnection();
        }

        return true;
    }


    /*public boolean deleteUser(User user) {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);   //Создаём соединение
            String SQL_INSERT = "INSERT INTO `holly-clicker-db`.`user` " +
                    "(`page-link`, `page-name`, `bday`, `bmonth`, `byear`, `number-friends`)" +
                    "VALUES (?, ?, ?, ?, ?, ?);";
            pstmt = connection.prepareStatement(SQL_INSERT);

            pstmt.setString(1, user.getProfileLink());
            pstmt.setString(2, user.getPageName());
            pstmt.setInt(3, user.getBday());
            pstmt.setInt(4, user.getBmonth());
            pstmt.setInt(5, user.getByear());
            pstmt.setInt(6, user.getNumberAllFriends());

            pstmt.executeUpdate();

            //Обновить запись
            //statement.executeUpdate("UPDATE users SET username = 'admin' where id = 1");


        } catch (Exception ex) {
            //выводим наиболее значимые сообщения
            Logger.getLogger(DataBaseWorker.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("-------------------------------");
        } finally {
            closePreparedStatement();
            closeConnection();
        }

        return true;
    }

    public boolean updateDateLastChecking(int profileID, String date) {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);   //Создаём соединение
            String SQL_UPDATE = "UPDATE `holly-clicker-db`.`user` SET `date-last-checking`=? WHERE `id-user`=?;";
            pstmt = connection.prepareStatement(SQL_UPDATE);

            pstmt.setString(1, date);
            pstmt.setInt(2, profileID);

            pstmt.executeUpdate();

        } catch (Exception ex) {
            //выводим наиболее значимые сообщения
            Logger.getLogger(DataBaseWorker.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("-------------------------------");
        } finally {
            closePreparedStatement();
            closeConnection();
        }

        return true;

    }*/

    //Технические вспомогательные методы
    public boolean closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                return true;
            } catch (SQLException ex) {
                System.out.println("Ошибка. Не закрыли connection");
                //Logger.getLogger(DataBaseWorker.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }

        }
        return true;
    }

    public boolean closeStatement() {
        if (stmt != null) {
            try {
                stmt.close();
                return true;
            } catch (SQLException ex) {
                System.out.println("Ошибка. Не закрыли statement");
                ex.getMessage();
                return false;
            }
        }
        return true;
    }

    public boolean closePreparedStatement() {
        if (pstmt != null) {
            try {
                pstmt.close();
                return true;
            } catch (SQLException ex) {
                System.out.println("Ошибка. Не закрыли PreparedStatement");
                ex.getMessage();
                return false;
            }
        }
        return true;
    }

}
