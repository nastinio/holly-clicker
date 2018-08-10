package ru.nastinio;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Main {

    public static void main(String[] args) {
        String log = "";
        String pass = "";

        String myProfileLink = "https://vk.com/id176464710";


        MainFunctionality mf = new MainFunctionality(log,pass);
        mf.setUpVK();


        mf.addUserToFriendList("https://vk.com/id437245261");

        /*//Собрали и записали в базу данных весь список друзей
        mf.setFullInfoListFriendsToDB(myProfileLink);
        //Забрали полный список пользователей из бд и распечатали его
        mf.printListFriend(mf.getListFriendsFromDB());*/


        /*User user = selWork.getFullInfoFromUserPage("https://vk.com/nikuly2004");
        user.display();
        DataBaseWorker dbWork = new DataBaseWorker();
        dbWork.insertUser(user);*/

        //System.out.println(selWork.getLinkToFirstTextPost("https://vk.com/antoinettemari"));

        //selWork.getFullInfoFromUserPage("https://vk.com/id226361909");

        //System.out.println(selWork.getLinkToFirstPost("https://vk.com/id226361909"));


        /*//Получение и печать всех друзей из бд
        DataBaseWorker dbWork = new DataBaseWorker();
        ArrayList<User> listUser = dbWork.getAllUsers();
        for (User currentUser: listUser) {
            currentUser.display();
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

        //selWork.likeSeveralPhotos(profileLink);



    }


}
