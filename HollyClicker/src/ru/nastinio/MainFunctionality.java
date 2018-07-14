package ru.nastinio;

import java.util.ArrayList;

public class MainFunctionality {

    private String login;
    private String password;

    private SeleniumWorker selWork;
    private DataBaseWorker dbWork;

    private boolean isOnline = false;

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
        int totalNumberFriend = listFriendsShortInfo.size()+1;
        for (User currentFriend : listFriendsShortInfo) {
            User user = selWork.getFullInfoFromUserPage(currentFriend.getProfileLink());
            //listFriendsFullInfo.add(user);
            dbWork.insertUser(user);
            System.out.println("Добавили пользователя #" +count+" из "+totalNumberFriend);
            user.display();
            System.out.println("======================================");
            count++;
        }
    }

    public ArrayList<User> getListFriendsFromDB(){
        return dbWork.getAllUsers();
    }

    public void printListFriend(ArrayList<User> list) {
        for (User currentUser : list) {
            currentUser.display();
        }
    }


}
