package ru.nastinio;


import ru.nastinio.Enums.ConstVK;
import ru.nastinio.Exceptions.DataBaseException;
import ru.nastinio.Exceptions.LoadException;
import ru.nastinio.Exceptions.SearchIDException;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        String log = "";
        String pass = "";


        MainFunctionality mf = new MainFunctionality(log, pass);
        mf.setUpVK();

        try {
            //Подготовим список сообщений для отправки
            String fileName = System.getProperty("user.dir") + "\\src\\msg\\Messages.txt";
            ArrayList<String> msgList = mf.prepareMsgList(fileName);
            //Отправим полученные сообщения из файла пользователям
            try{
                //mf.writeMessageToGroupMembers("https://vk.com/php2all", msgList, 20);
                mf.writeMessageToGroupMembers("https://vk.com/php2all", msgList,20);
            }catch (DataBaseException e) {
                e.printStackTrace();
            } catch (LoadException ee) {
                ee.printStackTrace();
            }

        }catch (FileNotFoundException e){
            e.printStackTrace();
        }


    }


}

class Testing {
    static DataBaseWorker db = new DataBaseWorker();

    public static void main(String[] args) {

        String log = "";
        String pass = "";
        

        Testing t = new Testing();

        SeleniumWorker sw = new SeleniumWorker();
        sw.authorization(log, pass);

        sw.likeSeveralPosts("https://vk.com/feed_forward",70);



        db.closeConnectionToDataBase();
    }



    public void testGetFullInfoListHostFriend(String log, String pass) {
        //Возьмем список всех друзей пользователя и запишем в бд
        SeleniumWorker sw = new SeleniumWorker();
        sw.authorization(log, pass);

        ArrayList<String> listMyFriends = sw.getHostListLinksFriendsByPage();

        for (int i = 25; i < listMyFriends.size(); i++) {
            System.out.println("-------------------");
            System.out.println(listMyFriends.get(i));
            try {
                User tempUser = sw.getFullInfoFromUserByPage(listMyFriends.get(i));
                tempUser.display();

                try {
                    db.insertUserToPotentialFriendsList(tempUser);
                } catch (SQLException e) {
                    System.out.println("Пользователь уже есть в бд");
                }
            } catch (LoadException e) {
                e.printStackTrace();
            }
        }
        /*for (String tempLink:listMyFriends){
            System.out.println("-------------------");
            System.out.println(tempLink);
            try {
                User tempUser = sw.getFullInfoFromUserByPage(tempLink);
                tempUser.display();

                try {
                    db.insertUserToPotentialFriendsList(tempUser);
                } catch (SQLException e) {
                    System.out.println("Пользователь уже есть в бд");
                }
            }catch (LoadException e){
                e.printStackTrace();
            }
        }*/
    }

    public void testGetFriendStatus(String log, String pass) {
        SeleniumWorker sw = new SeleniumWorker();
        sw.authorization(log, pass);

        String[] list = {"https://vk.com/id437245261", "https://vk.com/birarov", "https://vk.com/r17dptf", "https://vk.com/id437245261", "https://vk.com/id439606231"};
        for (String temp : list) {
            try {
                sw.sleep(3);
                System.out.println("---------------------------");
                System.out.println("Статус дружбы: " + sw.getFriendStatusByPage(temp));
                System.out.println(sw.getUserNameOnPage());
                System.out.println("---------------------------");
            } catch (LoadException e) {
                e.printStackTrace();
            }
        }
    }

    public void testGetFullInfoFromUserByPage(String log, String pass) {
        SeleniumWorker sw = new SeleniumWorker();
        sw.authorization(log, pass);

        DataBaseWorker db = new DataBaseWorker();

        String[] list = {"https://vk.com/birarov", "https://vk.com/amzk1", "https://vk.com/id499251069", "https://vk.com/urbanovich"};
        for (String temp : list) {
            try {
                User u = sw.getFullInfoFromUserOnPage(temp);
                u.display();
                //db.insertUserToPotentialFriendsList(u);
            } catch (LoadException e) {
                e.printStackTrace();
            }
        }
    }

    public void testGetUserDateBirthday(String log, String pass) {
        SeleniumWorker sw = new SeleniumWorker();
        sw.authorization(log, pass);

        System.out.println(sw.getUserDateBirthByPage("https://vk.com/lenulychka")); //без года
        System.out.println(sw.getUserDateBirthByPage("https://vk.com/feed_forward")); //без даты
        System.out.println(sw.getUserDateBirthByPage("https://vk.com/id226361909")); //норм
    }
}
