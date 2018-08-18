package ru.nastinio;

import ru.nastinio.Exceptions.AddToFriendlistException;
import ru.nastinio.Exceptions.LoadException;
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
    public void addUserToFriendList(String pageLink) {
        //Сначала добавим в друзья на сайте, если успешно - занесем в бд
        try {
            selWork.addUserToFriendList(pageLink);
            User currentUser = selWork.getStartInfoUserPage(pageLink);
            dbWork.insertUserToCurrentRequestToFriendList(currentUser, 1);
        } catch (AddToFriendlistException e) {
            System.out.println(e.getMessage());
        } catch (SearchIDException e) {
            System.out.println(e.getMessage());
        }
    }

    //Подготовим список для добавления в друзья
    public void getListPotentialFriendsByUserFriendList(String profileLink) {
        ArrayList<User> list = selWork.getUserFriendList(profileLink);
        for (int i = 7; i < 88; i++) {
            System.out.println("Начали обрабатывать");
            list.get(i).display();
            addUserToFriendList(list.get(i).getProfileLink());
            selWork.sleep(30);
        }
    }

    //Проверим заявки в друзья на подтверждение
    public void checkRequestToFriend() {
        ArrayList<User> list = dbWork.getAllPotentialFriends();
        int count = 0;
        for (User user : list) {
            System.out.printf("Проверяем %d/%d\n", count++, list.size());
            try {

                switch (selWork.checkFriendStatusByLink(user.getProfileLink())) {
                    case 1:
                        //Добавили в друзья
                        dbWork.updateStatusRequest(user.getProfileID(), 1);
                        //Потом можно добавить код на стартовое сообщение
                        break;
                    case 0:
                        break;
                    case -1:
                        //Отменили заявку
                        dbWork.updateStatusRequest(user.getProfileID(), -1);
                        //Добавить код на отписку
                        break;
                }
            } catch (LoadException e) {
                System.out.println(e.getMessage());
            }
        }

    }

    //Отправим сообщения подтвердившим заявку
    public void writeMessageToPotentialFriend() {

        selWork.writeMessageByLink("https://vk.com/id226361909", "Йееей, я механический отправитель смайликов :)");
    }

    //Отправить сообщения участникам группы
    public void writeMessageToGroupMembers(String groupLink) {
        try {
            //Получим список участников группы
            ArrayList<String> listMembers = selWork.getListGroupMembers(groupLink, 100);
            //Параллельно будем писать сообщения и заносить инфу в бд
            int i = 1;
            for (String currentMember : listMembers) {
                selWork.openUserPage(currentMember);

                try {
                    String currentUserCity = selWork.getUserCityOnPage();
                    System.out.println("#" + i + ": " + currentUserCity);
                    if (currentUserCity.equalsIgnoreCase("Санкт-Петербург")) {
                        System.out.println("Чувак из Питера! Йеей!");
                        //selWork.writeMessageByLink(currentMember,"Hello");
                    }
                } catch (LoadException e) {
                    System.out.println("#" + i + ": " + "город не указан");
                }
                System.out.println("-------------------------------");
                i++;

            }

        } catch (LoadException e) {
            System.out.println(e.getMessage());
        }

    }


}
