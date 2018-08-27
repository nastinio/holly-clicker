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
            connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Технические вспомогательные методы
    public void connect() throws SQLException {
        try {
            Class.forName(JDBC_DRIVER);                                                  //Загружаем драйвер
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);      //Создаём соединение
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void disconnect() throws SQLException {
        try {
            connection.close();
        } catch (SQLException e) {
            throw e;
        }
    }

    public void closeStatement() throws SQLException {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException ex) {
                throw ex;
            }
        }
    }
    public void closePreparedStatement() throws SQLException {
        if (pstmt != null) {
            try {
                pstmt.close();
            } catch (SQLException ex) {
                throw ex;
            }
        }
    }

    //Технические методы по окончанию работы
    public void closeConnectionToDataBase(){
        try {
            closePreparedStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            closeStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            disconnect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    //Для работы с таблицей потенциальных друзей
    public void insertUserToPotentialFriendsList(User user)throws SQLException {
        try {
            String SQL_INSERT = "INSERT INTO `holly-clicker-db`.`potential-friends-list` (`host-profile-link`, " +
                    "`user-id`, `user-profile-link`, `user-name`, `user-birthday`, `user-age`, `user-city`, " +
                    "`date-request`, `was-sent-request-to-friend`, `status-friend`, `was-sent-start-msg`, `comment`," +
                    "`count-friends`, `count-common-friends`, `count-followers`)" +
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

            pstmt = connection.prepareStatement(SQL_INSERT);

            pstmt.setString(1, user.getHostProfileLink());

            pstmt.setInt(2, user.getProfileID());
            pstmt.setString(3, user.getProfileLink());
            pstmt.setString(4, user.getPageName());

            pstmt.setString(5, user.getDateBirth());
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


        } catch (SQLException e) {
            throw e;
            //e.printStackTrace();
        }

    }

    public ArrayList getAllFromPotentialFriendsList(String hostUserProfileLink) throws SQLException {
        try {
            ArrayList<User> listUsers = new ArrayList<>();
            String sqlSelect = "SELECT * FROM `holly-clicker-db`.`potential-friends-list` where `host-profile-link` = '" + hostUserProfileLink + "';";
            System.out.println(sqlSelect);
            stmt = connection.createStatement();
            ResultSet resSet = stmt.executeQuery(sqlSelect);
            while (resSet.next()) {
                int userID = resSet.getInt("user-id");
                String userProfileLink = resSet.getString("user-profile-link");
                String userName = resSet.getString("user-name");

                User temp = new User(userID, userProfileLink, userName);

                temp.setDateBirth(resSet.getString("user-birthday"));
                temp.setCity(resSet.getString("user-city"));
                temp.setDateRequest(resSet.getString("date-request"));
                temp.setWasSentRequestToFriend(!(resSet.getInt("was-sent-request-to-friend") == 0));
                temp.setStatusFriend(resSet.getInt("status-friend"));
                temp.setWasSentStartMsg(!(resSet.getInt("was-sent-start-msg") == 0));
                temp.setComment(resSet.getString("comment"));
                temp.setCountFriends(resSet.getInt("count-friends"));
                temp.setCountCommonFriends(resSet.getInt("count-common-friends"));
                temp.setCountFollowers(resSet.getInt("count-followers"));

                listUsers.add(temp);
            }

            return listUsers;
        } catch (SQLException e) {
            throw e;
        } finally {
            closeStatement();
        }

    }

    /*public boolean updateStatusRequest(int profileID, int status) {
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
        }

        return true;
    }
*/

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






    /*

    private static Connection connection;
    private static Statement stmt;

    public static String getNickByLoginAndPass(String login, String pass) {
        String sql = String.format("SELECT nickname FROM main \n" +
                "where login = '%s'\n" +
                "and password= '%s'", login, pass);
        try {
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                return rs.getString(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }*/
}
