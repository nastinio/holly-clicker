package ru.nastinio;

import ru.nastinio.Enums.ConstDB;
import ru.nastinio.clientVK.User;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

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

    public DataBaseWorker() {
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

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /*
    * Методы для работы с основной таблицей потенциальных друзей
    * */

    public void insertUserToPotentialFriendsList(User user)throws SQLException {
        try {
            //connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);   //Создаём соединение

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
            pstmt.setInt(10, user.getStatusFriend());
            pstmt.setBoolean(11, user.wasSentStartMsg());

            pstmt.setString(12, user.getComment());

            pstmt.setInt(13, user.getCountFriends());
            pstmt.setInt(14, user.getCountCommonFriends());
            pstmt.setInt(15, user.getCountFollowers());

            pstmt.executeUpdate();


        } catch (SQLException e) {
            throw e;
            //e.printStackTrace();
        } finally {
            closePreparedStatement();
        }

    }

    public Map<Integer,User> getAllFromPotentialFriendsList(String hostUserProfileLink) throws SQLException {
        try {
            //connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);   //Создаём соединение
            //ArrayList<User> listUsers = new ArrayList<>();
            Map<Integer,User> mapUser = new HashMap< Integer, User>();
            String sqlSelect = "SELECT * FROM `holly-clicker-db`.`potential-friends-list` where `host-profile-link` = '" + hostUserProfileLink + "';";
            //System.out.println(sqlSelect);
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

                mapUser.put(userID,temp);
                //listUsers.add(temp);
            }

            return mapUser;
            //return listUsers;
        } catch (SQLException e) {
            throw e;
        } finally {
            closeStatement();
        }

    }

    public <T> void updatePotentialFriendsListTable(String hostUserProfileLink, int userID, ConstDB typeOfVariable, T countVariable) {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);   //Создаём соединение

            String SQL_UPDATE = "";
            stmt = connection.createStatement();

            switch (typeOfVariable) {
                case COMMENT:
                    SQL_UPDATE = "UPDATE `holly-clicker-db`.`potential-friends-list` SET `comment`='" + countVariable + "'" +
                            " WHERE `user-id`='" + userID + "' AND `host-profile-link`='" + hostUserProfileLink + "';";
                    break;
            }
            stmt.executeUpdate(SQL_UPDATE);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                closeStatement();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public <T extends Object> Object selectSpecialQueryPotentialFriendsListTable(String hostUserProfileLink, ConstDB typeOfVariable, T countVariable) {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);   //Создаём соединение

            String sqlSelect = "";

            switch (typeOfVariable) {
                case COUNT_SENT_MSG_GROUP_LINK:
                    //На вход метода нужно передать группу-источник, хранящуюся в комментарии
                    sqlSelect = "SELECT count(*) FROM `holly-clicker-db`.`potential-friends-list` " +
                            "where `host-profile-link`='"+hostUserProfileLink+"' AND `comment`='"+countVariable+"';";
                    break;
            }

            stmt = connection.createStatement();
            ResultSet resSet = stmt.executeQuery(sqlSelect);

            Object result = null;
            while (resSet.next()) {
                switch (typeOfVariable) {
                    case COUNT_SENT_MSG_GROUP_LINK:
                        result = resSet.getInt("count(*)");
                }

            }

            return result;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                closeStatement();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;

    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /*
    * Методы для работы с таблицей с техническими данными
    * */
    public int getCountTechnicalData(String hostUserProfileLink, String groupLink, ConstDB typeOfVariable) throws SQLException {
        try {
            String sqlSelect = "SELECT * FROM `holly-clicker-db`.`technical-data` where `host-profile-link` = '" + hostUserProfileLink + "' " +
                    "AND `group-link`='"+groupLink+"';";
            stmt = connection.createStatement();
            ResultSet resSet = stmt.executeQuery(sqlSelect);
            int count = 0;
            while (resSet.next()) {
                switch (typeOfVariable){
                    case COUNT_CHECKED_USER:
                        count = resSet.getInt("count-checked-users");
                        break;
                    case COUNT_SENT_MSG:
                        count = resSet.getInt("count-sent-msg");
                        break;
                }
            }
            return count;
        } catch (SQLException e) {
            throw e;
        } finally {
            closeStatement();
        }

    }

    public void updateCountTechnicalData(String hostUserProfileLink, String groupLink, ConstDB typeOfVariable,int countVariable) throws SQLException {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);   //Создаём соединение

            String SQL_UPDATE = "";

            switch (typeOfVariable){
                case COUNT_CHECKED_USER:
                    SQL_UPDATE = "UPDATE `holly-clicker-db`.`technical-data` SET `count-checked-users`=? WHERE `group-link`=?;";
                    break;
                case COUNT_SENT_MSG:
                    SQL_UPDATE = "UPDATE `holly-clicker-db`.`technical-data` SET `count-sent-msg`=? WHERE `group-link`=?;";
                    break;
            }

            pstmt = connection.prepareStatement(SQL_UPDATE);

            pstmt.setInt(1,countVariable);
            pstmt.setString(2, groupLink);

            pstmt.executeUpdate();

        } catch (Exception ex) {
            throw ex;
        }finally {
            closePreparedStatement();
        }
    }

    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



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

    turn null;
    }*/

