package ru.nastinio;

import ru.nastinio.Exceptions.AddToFriendlistException;
import ru.nastinio.Exceptions.SearchIDException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainFunctionality {

    private String login;
    private String password;
    private int hostID;

    private SeleniumWorker selWork;
    private DataBaseWorker dbWork;

    private boolean isOnline = false;

    SimpleDateFormat formatForDateNow = new SimpleDateFormat("YYYY-MM-dd");


    public MainFunctionality(String login, String password) {
        this.login = login;
        this.password = password;

        selWork = new SeleniumWorker();
        dbWork = new DataBaseWorker();

    }

    public boolean setUpVK() {
        int count = 0;
        while (!isOnline && count < 3) {
            if (selWork.authorization(login, password)) {
                isOnline = true;
                System.out.println("Авторизация прошла успешно");
                return true;
            }
            count++;
        }
        System.out.println("Не удалось авторизоваться");
        return false;

    }


    public void setFullInfoListFriendsToDB(String pageLink) {
        //Проход по всем друзьям пользователя и запись в бд
        ArrayList<User> listFriendsShortInfo = selWork.getUserFriendList(pageLink);
        //ArrayList<User> listFriendsFullInfo = new ArrayList<>();
        int count = 1;
        int totalNumberFriend = listFriendsShortInfo.size() + 1;
        for (User currentFriend : listFriendsShortInfo) {
            User user = selWork.getFullInfoFromUserPage(currentFriend.getProfileLink());
            //listFriendsFullInfo.add(user);
            dbWork.insertUser(user);
            System.out.println("Добавили пользователя #" + count + " из " + totalNumberFriend + " в бд");
            user.display();
            System.out.println("======================================");
            count++;
        }
    }

    public ArrayList<User> getListFriendsFromDB() {
        return dbWork.getAllUsers();
    }

    public void printListFriend(ArrayList<User> list) {
        for (User currentUser : list) {
            currentUser.display();
        }
    }

    public void getLikesAllFriends(int numberLikesForEachUser) {
        ArrayList<User> listUsers = dbWork.getAllUsers();
        for (User currentUser : listUsers) {
            selWork.likeSeveralPostsOnPage(currentUser.getProfileLink(), numberLikesForEachUser);
            dbWork.updateDateLastChecking(currentUser.getProfileID(), formatForDateNow.format(new Date()));
            System.out.println("Пролайкали " + currentUser.getPageName() + " и обновили дату в базе");
            System.out.println("======================================================================");
        }
    }


    //Добавление в друзья
    public void addUserToFriendList(String pageLink){
        //Сначала добавим в друзья на сайте, если успешно - занесем в бд
        try {
            selWork.addUserToFriendList(pageLink);
            User currentUser = selWork.getStartInfoUserPage(pageLink);
            dbWork.insertUserToCurrentRequestToFriendList(currentUser,1);
        }catch(AddToFriendlistException e){
            System.out.println(e.getMessage());
        } catch (SearchIDException e) {
            System.out.println(e.getMessage());
        }
    }

}
