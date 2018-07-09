package ru.nastinio;

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

    DataBaseWorker(){
        try {
            Class.forName(JDBC_DRIVER);     //Загружаем драйвер
        }catch (Exception ex) {
            //выводим наиболее значимые сообщения
            Logger.getLogger(DataBaseWorker.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("-------------------------------");
        }
    }

    public void insertUser(User user) {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);   //Создаём соединение
            String SQL_INSERT = "INSERT INTO `holly-clicker-db`.`user` " +
                    "(`id-user`, `page-link`, `page-name`, `bday`, `bmonth`, `byear`, `number-friends`)" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?);";
            pstmt = connection.prepareStatement(SQL_INSERT);

            pstmt.setInt(1,user.getProfileID());
            pstmt.setString(2,user.getProfileLink());
            pstmt.setString(3,user.getPageName());
            pstmt.setInt(4,user.getBday());
            pstmt.setInt(5,user.getBmonth());
            pstmt.setInt(6,user.getByear());
            pstmt.setInt(7,user.getNumberOfFriends());

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

    }

    public ArrayList getAllUsers() {
        ArrayList<User> listUsers = new ArrayList<>();
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);   //Создаём соединение
            String SQL_SELECT = "SELECT * FROM `holly-clicker-db`.user";
            stmt = connection.createStatement();
            ResultSet resSet = stmt.executeQuery(SQL_SELECT);
            while (resSet.next()){
                int profileID = resSet.getInt("id-user");
                String profileLink = resSet.getString("page-link");
                String pageName = resSet.getString("page-name");;
                int bday = resSet.getInt("bday");
                int bmonth = resSet.getInt("bmonth");
                int byear = resSet.getInt("byear");
                int numberOfFriends = resSet.getInt("number-friends");

                User tempUser = new User(profileLink,pageName,profileID,bday,bmonth,byear,numberOfFriends);
                listUsers.add(tempUser);

                /*System.out.println("Номер в выборке #" + resSet.getRow() + "\t Номер в базе #" + resSet.getInt("id-user")
                        + "\t" + resSet.getString("page-name"));*/
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

    public boolean deleteUser(User user){
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);   //Создаём соединение
            String SQL_INSERT = "INSERT INTO `holly-clicker-db`.`user` " +
                    "(`page-link`, `page-name`, `bday`, `bmonth`, `byear`, `number-friends`)" +
                    "VALUES (?, ?, ?, ?, ?, ?);";
            pstmt = connection.prepareStatement(SQL_INSERT);

            pstmt.setString(1,user.getProfileLink());
            pstmt.setString(2,user.getPageName());
            pstmt.setInt(3,user.getBday());
            pstmt.setInt(4,user.getBmonth());
            pstmt.setInt(5,user.getByear());
            pstmt.setInt(6,user.getNumberOfFriends());

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

    //Технические вспомогательные методы
    public boolean closeConnection(){
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

    public boolean closeStatement(){
        if ( stmt!= null) {
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

    public boolean closePreparedStatement(){
        if ( pstmt!= null) {
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