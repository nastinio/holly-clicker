package ru.nastinio;


public class User{

    private String profileLink;
    private String pageName;


    public User(String profileLink, String pageName) {
        this.profileLink = profileLink;
        this.pageName = pageName;
    }

    public void display(){
        System.out.println("Page name:      " + pageName);
        System.out.println("Profile link:   " + profileLink);
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

}
