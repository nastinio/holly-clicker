package ru.nastinio;


public class Main {

    public static void main(String[] args) {
        /*String profileLink = "https://vk.com/id437245261";
        User tempUser = new User(profileLink, "Настя Дорина");*/


        String profileLink = "https://vk.com/id176464710";
        User tempUser = new User(profileLink, "Настя Бессарабова");

        SeleniumWorker selWork = new SeleniumWorker();
        //selWork.authorization(log, pass);

        String photoLink = "https://vk.com/id437245261?z=photo437245261_456239018%2Fphotos437245261";
        String postLink = "https://vk.com/id437245261?w=wall437245261_4%2Fall";

        //String startWallPost = "https://vk.com/id226361909?w=wall226361909_2159%2Fall";
        //selWork.likePosts(startWallPost);



        //selWork.likePostByLink(postLink, ConstVK.WL_POST, ConstVK.DISLIKE);
        //selWork.likePostByLink(postLink, ConstVK.WL_POST, ConstVK.LIKE);

        //selWork.likePostByLink(photoLink, ConstVK.PHOTO_POST, ConstVK.LIKE);

        //selWork.likeProfilePhoto(tempUser.getProfileLink());

        //selWork.likesAllPhotos(profileLink);

        /*String linkFriends = ".//*[@id='l_fr']/a/span/span[1]";
        System.out.println("Нажали на друзей: " + selWork.ClickByXPath(linkFriends));
        selWork.getFriendsList();*/


    }


}
