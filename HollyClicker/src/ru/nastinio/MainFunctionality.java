package ru.nastinio;

import ru.nastinio.Exceptions.DataBaseException;
import ru.nastinio.Exceptions.LoadException;
import ru.nastinio.Exceptions.SearchIDException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class MainFunctionality {

    private String login;
    private String password;
    private String hostProfileLink;

    private SeleniumWorker selWork;
    private DataBaseWorker dbWork;
    private FileWorker fileWorker;

    private boolean isOnline = false;

    SimpleDateFormat formatForDateNow = new SimpleDateFormat("YYYY-MM-dd");


    public MainFunctionality(String login, String password) {
        this.login = login;
        this.password = password;

        selWork = new SeleniumWorker();
        dbWork = new DataBaseWorker();
        fileWorker = new FileWorker();

    }


    public boolean setUpVK() {
        try {
            selWork.authorization(login, password);
            isOnline = true;
            try{
                hostProfileLink = selWork.getHostPageLink();
            }catch(LoadException e){
                throw e;
            }
        } catch (LoadException e) {
            //count++;
        }


        return false;

    }


    /*public void setFullInfoListFriendsToDB(String pageLink) {
        //Проход по всем друзьям пользователя и запись в бд
        ArrayList<User> listFriendsShortInfo = selWork.getUserListLinksFriendsByPage(pageLink);
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
    }*/


    public void printListFriend(ArrayList<User> list) {
        for (User currentUser : list) {
            currentUser.display();
        }
    }

    /*public void getLikesAllFriends(int numberLikesForEachUser) {
        ArrayList<User> listUsers = dbWork.getAllUsers();
        for (User currentUser : listUsers) {
            selWork.likeSeveralPostsOnPage(currentUser.getProfileLink(), numberLikesForEachUser);
            dbWork.updateDateLastChecking(currentUser.getProfileID(), formatForDateNow.format(new Date()));
            System.out.println("Пролайкали " + currentUser.getPageName() + " и обновили дату в базе");
            System.out.println("======================================================================");
        }
    }*/


    //Добавление в друзья
    public void addUserToFriendList(String pageLink) {
        //Сначала добавим в друзья на сайте, если успешно - занесем в бд
        try {
            selWork.addUserToFriendList(pageLink);
            User currentUser = selWork.getStartInfoUserByPage(pageLink);
            //dbWork.insertUserToCurrentRequestToFriendList(currentUser, 1);
        } catch (LoadException e) {
            throw e;
        } catch (SearchIDException e) {
            System.out.println(e.getMessage());
        }
    }

    //Подготовим список для добавления в друзья
    public void getListPotentialFriendsByUserFriendList(String profileLink) {
        ArrayList<String> list = selWork.getUserListLinksFriendsByPage(profileLink);
        for (int i = 7; i < 88; i++) {
            System.out.println("Начали обрабатывать");
            System.out.println(list.get(i));
            addUserToFriendList(list.get(i));
            selWork.sleep(30);
        }
    }


    //Отправим сообщения подтвердившим заявку
    public void writeMessageToPotentialFriend() {

        selWork.writeMessageByPage("https://vk.com/id226361909", "Йееей, я механический отправитель смайликов :)");
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Отправить сообщения участникам группы
    /*
     * На вход подается ссылка на группу, сообщение и количество человек, которым нужно отправить сообщение
     * Забирает список ссылок на пользователей, пропускает их через необходимые фильтры,
     * пишет сообщение и записывает в бд
     *
     * */

    int countCheckedMember;
    Map<Integer, User> mapExistingInDBUser;

    public void writeMessageToGroupMembers(String groupLink, ArrayList<String> msgList, int totalCountMsg) throws DataBaseException, LoadException {
        try {
            mapExistingInDBUser = dbWork.getAllFromPotentialFriendsList(hostProfileLink);
            try {
                //Получим список участников группы со страницы
                int countSendMsg = 0;           //Количество уже отправленных сообщений
                int currentTotalSize = 0;       //Общее количество сообщений, которое нужно отправить

                countCheckedMember = 0;         //Не влияет на логику, только для отображения

                while (countSendMsg < totalCountMsg) {
                    System.out.println("------------------------------------------------------------");
                    System.out.println("Начали обрабатывать блок");

                    //Получаем текущий блок ссылок на страницы пользователей
                    ArrayList<String> list = prepareShortListMember(currentTotalSize, totalCountMsg, countSendMsg, groupLink);

                    //Рассылаем сообщения, пропуская ссылки через фильтры и параллельно записываем пользователей в бд
                    countSendMsg = writeMsgToShortListGroupMember(list, totalCountMsg, countSendMsg, groupLink, msgList);

                    System.out.println("По итогу обработки блока написали сообщений: " + countSendMsg);
                    System.out.println("------------------------------------------------------------");
                    currentTotalSize = list.size();

                }


            } catch (LoadException e) {
                throw e;
                //System.out.println(e.getMessage());
            }

        } catch (SQLException e) {
            throw new DataBaseException("Не удалось получить список пользователей из бд");
        }



    }

    private ArrayList<String> prepareShortListMember(int currentTotalSize, int totalCountMsg, int countSendMsg, String groupLink) {
        int previousTotalSize = currentTotalSize;
        currentTotalSize = previousTotalSize + (totalCountMsg - countSendMsg) * 3;
        int currentShortSize = currentTotalSize - previousTotalSize;

       /* System.out.println("previousTotalSize: "+previousTotalSize);
        System.out.println("currentTotalSize:  "+currentTotalSize);
        System.out.println("currentShortSize:  "+currentShortSize);*/


        ArrayList<String> listTotalMembers = selWork.getListGroupMembers(groupLink, currentTotalSize);
        //System.out.println("Получили расширенный список размера: " + listTotalMembers.size());
        ArrayList<String> listShortMembers = new ArrayList();
        //Обрезаем массив
        for (int i = 0; i < currentShortSize; i++) {
            listShortMembers.add(listTotalMembers.get(previousTotalSize + i));
        }
        //System.arraycopy(listTotalMembers, previousTotalSize, listShortMembers, 0, currentShortSize);
        //System.out.println("Обрезали его до размера: "  + listShortMembers.size());

        return listShortMembers;
    }

    private int writeMsgToShortListGroupMember(ArrayList<String> list, int totalCountMsg, int countSendMsg, String groupLink, ArrayList<String> msgList) {
        int numberMsgToSent = countSendMsg;
        for (String currentMember : list) {
            try {
                selWork.openUserPage(currentMember);
                try {
                    //Определяем фильтры, пока только город и писали ли ему уже
                    String currentUserCity = selWork.getUserCityOnPage();
                    System.out.println("=============================================");
                    System.out.println("Проверяем пользователя #" + (++countCheckedMember));
                    //System.out.println("#" + countCheckingUser + ": " + currentUserCity);
                    if (currentUserCity.equalsIgnoreCase("Санкт-Петербург")) {
                        System.out.println("Чувак из Питера! Йеей!");

                        try {
                            User temp = selWork.getFullInfoFromUserOnPage(currentMember);
                            temp.display();

                            //Заполним поля, связанные с написанием сообщения
                            temp.setWasSentStartMsg(true);
                            temp.setComment("Из группы: " + groupLink);
                            //Запишем в бд
                            try {
                                if (!mapExistingInDBUser.containsKey(temp.getProfileID())) {
                                    //В бд нет такого пользователя
                                    dbWork.insertUserToPotentialFriendsList(temp);
                                    selWork.writeMessageByPage(currentMember, msgList.get(numberMsgToSent));

                                    numberMsgToSent++;
                                    countSendMsg++;

                                    if(countSendMsg == msgList.size()){
                                        numberMsgToSent = 0;
                                    }
                                    System.out.println("Написали сообщений: " + countSendMsg + " из " + totalCountMsg);
                                    if (countSendMsg == totalCountMsg) {
                                        return countSendMsg;
                                    }

                                    //Искуственная пауза
                                    Random rnd = new Random(System.currentTimeMillis());
                                    int max = 100;
                                    int min = 50;
                                    int timeSleep = min + rnd.nextInt(max - min + 1);
                                    selWork.sleep(timeSleep);
                                }
                            } catch (SQLException e) {
                                System.out.println("Стартовое сообщение уже было отправлено");
                                //e.printStackTrace();
                            }

                        } catch (LoadException e) {
                            throw new LoadException(e.getMessage());
                        }
                    }

                } catch (LoadException e) {
                    System.out.println("#" + countCheckedMember + ": " + "город не указан");
                }
            } catch (LoadException e) {
                System.out.println("Не удалось загрузить стрвницу пользователя");
                e.printStackTrace();
            }
        }
        return countSendMsg;
    }

    public ArrayList<String> prepareMsgList(String fileName) throws FileNotFoundException {
        FileWorker fileWorker = new FileWorker();

        try{
            String msgAllStr = fileWorker.read(fileName);
            ArrayList<String> listMsg = new ArrayList<>();
            for (String temp : msgAllStr.split("\n{2,}")) {
                listMsg.add(temp);
            }
            return listMsg;
        }catch (FileNotFoundException e){
            throw e;
        }

    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
