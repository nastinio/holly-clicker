package ru.nastinio.clientVK;


import java.time.LocalDate;
import java.time.Period;

public class User {

    private int profileID;              //Исходный ID пользователя
    private String profileLink;         //Текущая ссылка на профиль
    private String pageName;            //Имя профиля
    private String hostProfileLink;     //Пользователь, который работал в системе

    //Информация о дне рождения
    /*private int bday = 0;
    private int bmonth = 0;
    private int byear = 0;*/
    private String dateBirth = "0000-00-00";
    private int age = 0;

    private String city = null;         //Текущий город

    //Вспомогательные метки
    private String dateRequest = null;
    private boolean wasSentRequestToFriend = false;
    private int statusFriend = 0;                //-2 отклонил заявку, -1 - не ответил на заявку, 1 - добавил в друзья, 0 - не добавляли в друзья
    private boolean wasSentStartMsg = false;
    private String comment = null;

    private int countFriends = 0;
    private int countCommonFriends = 0;
    private int countFollowers = 0;

    //Конструктор с необходимым минимумом
    public User(int profileID, String profileLink, String profileName) {
        this.profileID = profileID;
        this.profileLink = profileLink;
        this.pageName = profileName;
    }

    /*//Подпорка
    public User(String profileLink, String pageName) {
        this.profileLink = profileLink;
        this.pageName = pageName;
    }*/

    //Полный для работы с бд
    public User(int profileID, String profileLink, String pageName, String hostProfileLink, String dateBirth, String city, String dateRequest,
                boolean wasSentRequestToFriend, int statusFriend, boolean wasSentStartMsg, String comment, int countFriends, int countCommonFriends, int countFollowers) {
        this.profileID = profileID;
        this.profileLink = profileLink;
        this.pageName = pageName;
        this.hostProfileLink = hostProfileLink;
        this.dateBirth=dateBirth;
        this.age = calculateAge();
        this.city = city;
        this.dateRequest = dateRequest;
        this.wasSentRequestToFriend = wasSentRequestToFriend;
        this.statusFriend = statusFriend;
        this.wasSentStartMsg = wasSentStartMsg;
        this.comment = comment;
        this.countFriends = countFriends;
        this.countCommonFriends = countCommonFriends;
        this.countFollowers = countFollowers;
    }

    public void display() {
        System.out.println("--------------------------------------");
        System.out.println("Page name:      " + pageName);
        System.out.println("Profile link:   " + profileLink);
        System.out.println("Profile ID:     " + profileID);
        System.out.println("Is my friend:   " + statusFriend);
        System.out.println("Birthday:       " + dateBirth);
        System.out.println("Age:            " + age);
        System.out.println("City:           " + city);
        System.out.println("----------------");
        System.out.println("Was sent start message: " + wasSentStartMsg);
        System.out.println("Comment:                " + comment);
        System.out.println("All friends:    " + countFriends);
        System.out.println("Common friends: " + countCommonFriends);
        System.out.println("Followers:      " + countFollowers);
        System.out.println("--------------------------------------");
    }

    public int calculateAge() {
        //System.out.println("Получили на вход в calculateAge: " + dateBirth);
        if(dateBirth==null|dateBirth.equalsIgnoreCase("0000-00-00")){
            return 0;
        }
        int byear = 0;
        int bmonth = 0;
        int bday =0;
        try{
            byear = Integer.parseInt(dateBirth.substring(0,4));
        }catch (NumberFormatException e){
            byear = 0;
        }
        try{
            bmonth = Integer.parseInt(dateBirth.substring(5,7));
        }catch (NumberFormatException e){}
        try{
            bday = Integer.parseInt(dateBirth.substring(8,dateBirth.length()));
        }catch (NumberFormatException e){}

        LocalDate currentDate = LocalDate.now();
        if(byear==0){
            return 0;
        }else{
            if(bmonth==0){
                return currentDate.getYear()-byear;
            }else{
                if(bday == 0){
                    return currentDate.getYear()-byear;
                }else{
                    LocalDate birthDate = LocalDate.of(byear, bmonth, bday);
                    return Period.between(birthDate, currentDate).getYears();
                }
            }
        }
    }


    //Геттеры и сеттеры
    public int getProfileID() {
        return profileID;
    }

    public void setProfileID(int profileID) {
        this.profileID = profileID;
    }

    public String getProfileLink() {
        return profileLink;
    }

    public void setProfileLink(String profileLink) {
        this.profileLink = profileLink;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getHostProfileLink() {
        return hostProfileLink;
    }

    public void setHostProfileLink(String hostProfileLink) {
        this.hostProfileLink = hostProfileLink;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDateRequest() {
        return dateRequest;
    }

    public void setDateRequest(String dateRequest) {
        this.dateRequest = dateRequest;
    }

    public boolean wasSentRequestToFriend() {
        return wasSentRequestToFriend;
    }

    public void setWasSentRequestToFriend(boolean wasSentRequestToFriend) {
        this.wasSentRequestToFriend = wasSentRequestToFriend;
    }

    public int getStatusFriend() {
        return statusFriend;
    }

    public void setStatusFriend(int statusFriend) {
        this.statusFriend = statusFriend;
        if(statusFriend==0){
            wasSentRequestToFriend = false;
        }else{
            wasSentRequestToFriend =true;
        }
    }

    public boolean wasSentStartMsg() {
        return wasSentStartMsg;
    }

    public void setWasSentStartMsg(boolean wasSentStartMsg) {
        this.wasSentStartMsg = wasSentStartMsg;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getCountFriends() {
        return countFriends;
    }

    public void setCountFriends(int countFriends) {
        this.countFriends = countFriends;
    }

    public int getCountCommonFriends() {
        return countCommonFriends;
    }

    public void setCountCommonFriends(int countCommonFriends) {
        this.countCommonFriends = countCommonFriends;
    }

    public int getCountFollowers() {
        return countFollowers;
    }

    public void setCountFollowers(int countFollowers) {
        this.countFollowers = countFollowers;
    }

    public String getDateBirth() {
        return dateBirth;
    }

    public void setDateBirth(String dateBirth) {
        this.dateBirth = dateBirth;
        this.age = calculateAge();
    }
}
