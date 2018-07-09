package ru.nastinio;


import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {



        String log = "";
        String pass = "";

        String profileLink = "https://vk.com/id176464710";
        //User tempUser = new User(profileLink, "Настя Бессарабова");


        SeleniumWorker selWork = new SeleniumWorker();
        selWork.authorization(log, pass);

        //System.out.println(selWork.getLinkToFirstTextPost("https://vk.com/antoinettemari"));

        //selWork.getFullInfoUser("https://vk.com/id226361909");

        //System.out.println(selWork.getLinkToFirstPost("https://vk.com/id226361909"));


        /*//Получение и печать всех друзей из бд
        DataBaseWorker dbWork = new DataBaseWorker();
        ArrayList<User> listUser = dbWork.getAllUsers();
        for (User currentUser: listUser) {
            currentUser.display();
        }*/


        /*//Проход по всем друзьям пользователя и запись в бд
        DataBaseWorker dbWork = new DataBaseWorker();
        ArrayList<User> listFriendsShortInfo = selWork.getUserFriendList(profileLink);
        ArrayList<User> listFriendsFullInfo = new ArrayList<>();
        for (User currentFriend:listFriendsShortInfo) {
            User user = selWork.getFullInfoUser(currentFriend.getProfileLink());
            listFriendsFullInfo.add(user);
            dbWork.insertUser(user);
            user.display();
            System.out.println("-------------------");
        }*/

        /*for (User currentFriend:listFriendsFullInfo) {
            currentFriend.display();
            System.out.println("-------------------");
        }*/


        String photoLink = "https://vk.com/id437245261?z=photo437245261_456239018%2Fphotos437245261";
        String postLink = "https://vk.com/id437245261?w=wall437245261_4%2Fall";

        String startWallPost = "https://vk.com/id226361909?w=wall226361909_2159%2Fall";
        //selWork.likeSeveralPosts(startWallPost,200);

        //selWork.likePostByLink(postLink, ConstVK.WL_POST, ConstVK.DISLIKE);
        //selWork.likePostByLink(postLink, ConstVK.WL_POST, ConstVK.LIKE);

        //selWork.likePostByLink(photoLink, ConstVK.PHOTO_POST, ConstVK.LIKE);

        //selWork.likeProfilePhoto(tempUser.getProfileLink());

        //selWork.likesSeveralPhotos(profileLink);



    }


}
