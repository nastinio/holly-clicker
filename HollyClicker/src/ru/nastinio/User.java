package ru.nastinio;


public class User{

    private String profileID;           //Первоначальный ID пользователя
    private String profileLink;         //Текущая ссылка на профиль
    private String pageName;            //Имя профиля

    //Информация о дне рождения
    private int bday = 0;
    private int bmonth = 0;
    private int byear = 0;

    //Вспомогательные штуки
    private int numberOfFriends = 0;

    public User(String profileLink) {
        this.profileLink = profileLink;
    }

    public User(String profileLink, String pageName) {
        this.profileLink = profileLink;
        this.pageName = pageName;
    }

    public void display(){
        System.out.println("Page name:      " + pageName);
        System.out.println("Profile link:   " + profileLink);
        System.out.printf("Birthday:       %d.%d.%d\n",bday,bmonth,byear);
        System.out.println("Number friends: " + numberOfFriends);
    }

    public String getProfileID() {
        return profileID;
    }

    public void setProfileID(String profileID) {
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

    public int getBday() {
        return bday;
    }

    public void setBday(int bday) {
        this.bday = bday;
    }

    public int getBmonth() {
        return bmonth;
    }

    public void setBmonth(int bmonth) {
        this.bmonth = bmonth;
    }

    public int getByear() {
        return byear;
    }

    public void setByear(int byear) {
        this.byear = byear;
    }

    public int getNumberOfFriends() {
        return numberOfFriends;
    }

    public void setNumberOfFriends(int numberOfFriends) {
        this.numberOfFriends = numberOfFriends;
    }
}
