package ru.nastinio;


public class User {

    private int profileID;              //Исходный ID пользователя
    private String profileLink;         //Текущая ссылка на профиль
    private String pageName;            //Имя профиля

    //Информация о дне рождения
    private int bday = 0;
    private int bmonth = 0;
    private int byear = 0;

    //Вспомогательные штуки
    private int numberAllFriends = 0;
    private int numberCommonFriends = 0;
    private int numberFollowers = 0;
    private boolean isMyFriend = false;

    public User(String profileLink) {
        this.profileLink = profileLink;
    }

    public User(String profileLink, String pageName) {
        this.profileLink = profileLink;
        this.pageName = pageName;
    }


    public User(String profileLink, String pageName, int profileID, int bday, int bmonth, int byear, int numberAllFriends) {
        this.profileLink = profileLink;
        this.pageName = pageName;
        this.profileID = profileID;
        this.bday = bday;
        this.bmonth = bmonth;
        this.byear = byear;
        this.numberAllFriends = numberAllFriends;
    }

    public void display() {
        System.out.println("--------------------------------------");
        System.out.println("Page name:      " + pageName);
        System.out.println("Profile link:   " + profileLink);
        System.out.println("Profile ID:     " + profileID);
        System.out.println("Is my friend:   " + isMyFriend);
        System.out.printf("Birthday:       %d.%d.%d\n", bday, bmonth, byear);
        System.out.println("All friends:    " + numberAllFriends);
        System.out.println("Common friends: " + numberCommonFriends);
        System.out.println("Followers:      " + numberFollowers);
        System.out.println("--------------------------------------");
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

    public int getNumberAllFriends() {
        return numberAllFriends;
    }

    public void setNumberAllFriends(int numberAllFriends) {
        this.numberAllFriends = numberAllFriends;
    }


    public int getProfileID() {
        return profileID;
    }

    public void setProfileID(int profileID) {
        this.profileID = profileID;
    }

    public int getNumberCommonFriends() {
        return numberCommonFriends;
    }

    public void setNumberCommonFriends(int numberCommonFriends) {
        this.numberCommonFriends = numberCommonFriends;
    }

    public int getNumberFollowers() {
        return numberFollowers;
    }

    public void setNumberFollowers(int numberFollowers) {
        this.numberFollowers = numberFollowers;
    }

    public boolean isMyFriend() {
        return isMyFriend;
    }

    public void setIsMyFriend(boolean myFriend) {
        isMyFriend = myFriend;
    }
}
