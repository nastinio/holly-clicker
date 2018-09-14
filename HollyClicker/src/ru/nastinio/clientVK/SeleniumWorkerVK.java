package ru.nastinio.clientVK;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.nastinio.Enums.ConstVK;
import ru.nastinio.Exceptions.LoadException;
import ru.nastinio.Exceptions.SearchIDException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class SeleniumWorkerVK {

    protected static WebDriver driver;
    protected WebDriverWait wait;

    public HelpFunctionalityVK hp = new HelpFunctionalityVK();

    private final String VK_URL = "https://vk.com/";
    private String hostPageLink;

    //Просто полезный пирожок для наглядности
    protected String separator = "=============================================";

    //Количество друзе в одном отображаемом блоке
    //Надо бы получить программно, но пока пусть так
    protected int numberFriendsOnBox = 15;
    protected int numberPostsOnBlock = 10;

    //Безопасной количество лайков, при которых действия не считаются подозрительными
    protected int safetyNumberLikes = 30;

    public SeleniumWorkerVK() {
        /*//Штука, для подключения на компе без драйвера FireFox'a
        String driverDireсtory =System.getProperty("user.dir")+ "\\src\\drivers\\geckodriver.exe";
        System.setProperty("webdriver.gecko.driver",driverDireсtory);*/

        try {
            driver = new FirefoxDriver();
            wait = new WebDriverWait(driver, 5);
        } catch (org.openqa.selenium.WebDriverException we) {
            System.out.println("Ошибка в конструкторе SeleniumWorkerVK");
            we.getMessage();
        }

    }

    //Стартовые методы
    public void authorization(String login, String password) {
        //Элементы для авторизации
        String INPUT_LOGIN_XPATH = ".//*[@id='index_email']";         //Поле ввода логина
        String INPUT_PASSWORD_XPATH = ".//*[@id='index_pass']";       //Поле ввода пароля
        String BTN_LOG_XPATH = ".//*[@id='index_login_button']";      //Кнопка войти


        System.out.println(separator);
        System.out.println("Start authorization");
        driver.get(VK_URL);

        try{
            waitLoadElementByTypeExp(ConstVK.WELCOME_PAGE);

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(INPUT_LOGIN_XPATH)));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(INPUT_PASSWORD_XPATH)));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(BTN_LOG_XPATH)));

            WebElement inputLogin = driver.findElement(By.xpath(INPUT_LOGIN_XPATH));
            WebElement inputPassword = driver.findElement(By.xpath(INPUT_PASSWORD_XPATH));
            WebElement btnLogin = driver.findElement(By.xpath(BTN_LOG_XPATH));

            inputLogin.sendKeys(login);
            inputPassword.sendKeys(password);

            btnLogin.click();
            //Дописать повторны вход
            try{
                waitLoadElementByTypeExp(ConstVK.HOST_USER_PAGE);
                System.out.println("Result authorization: true");
                sleep(2);

                //Заполним личные данные, что пригодятся потом
                hostPageLink = findHostPageLink();
            }catch (LoadException e){
                //Вручную введем капчу
                try{
                    sleep(100);
                    waitLoadElementByTypeExp(ConstVK.HOST_USER_PAGE);
                    System.out.println("Result authorization: true");
                    sleep(2);

                    //Заполним личные данные, что пригодятся потом
                    hostPageLink = findHostPageLink();
                }catch (LoadException ex){
                    throw new LoadException("Ошибка входа");
                }
            }
        }catch (LoadException e){
            throw new LoadException("Ошибка загрузки стартовой страницы");
        }

    }

    public String findHostPageLink() throws LoadException {
        try {
            String btnMyPageXPath = "//*[@id=\"l_pr\"]/a/span/span[3]";
            driver.findElement(By.xpath(btnMyPageXPath)).click();
            try {
                waitLoadElementByTypeExp(ConstVK.USER_PAGE);
                return driver.getCurrentUrl();
            } catch (LoadException e) {
                throw new LoadException("Не удалось загрузить рабочую страницу");
            }
        } catch (LoadException e) {
            throw new LoadException("Не удалось найти кнопку 'Моя страница'");
        }


    }
    public String getHostPageLink() {
        return hostPageLink;
    }


    //Методы, связанные с работой со списками друзей
    public ArrayList<String> getHostListLinksFriendsByPage() throws LoadException{
        try{
            String myFriendsLinkXpath = "//*[@id=\"l_fr\"]/a/span/span[2]";
            waitLoadElementExp(myFriendsLinkXpath);

            driver.findElement(By.xpath(myFriendsLinkXpath)).click();

            try{
                return getUserListLinksFriendsOnPage();
            }catch (LoadException e){
                throw new LoadException("getUserListLinksFriendsOnPage: кинул исключение");
            }
        }catch (LoadException e){
            throw new LoadException("Не удалось перейти на вкладку 'Друзья'");
        }
    }
    public ArrayList<String> getUserListLinksFriendsByPage(String pageLink) throws LoadException {
        try {
            openUserPage(pageLink);
            try {
                //Вызываем метод, который соберет ссылки на страницы друзей
                return getUserListLinksFriendsOnPage();
            } catch (LoadException e) {
                throw e;
            }

        } catch (LoadException e) {
            throw new LoadException("getUserListLinksFriendsByPage: не удалось загрузить страницу пользователя");
        }

    }
    private ArrayList<String> getUserListLinksFriendsOnPage() throws LoadException {
        //Вернет лист ссылок на страницы пользователей

        // Локаторы для получения информации о друге из укороченного списка
        // По отдельности не используются, т.к. после каждой части неодходимо добавить
        // индекс текущего блока друзей и индекс друга в блоке
        // Полный локатор на примере 1-ого в списке друга:
        // //*[@id="list_content"]//div[1]//div[contains(@class,'friends_user_row')][1]//div[contains(@class,'friends_field_title')]//a
        String FRIEND_LIST_CONT_PART1 = "//*[@id=\"list_content\"]//div";                                    //+[currentBlock]
        String FRIEND_LIST_ELEMENT_FULL_USER_INFO_PART2 = "//div[contains(@class,'friends_user_row')]";     //+[currentElementInBlock]
        //Ссылка и имя страницы конкретного пользователя (Обрезанная часть. Саму по себе использовать нельзя)
        String FRIEND_LIST_ELEMENT_FIELD_TITLE_PART3 = "//div[contains(@class,'friends_field_title')]//a";

        //Общее количество друзей
        String NUMBER_OF_FRIENDS_ON_PAGE_LIST_FRIENDS = "//*[@id=\"friends_tab_all\"]/a/span";
        String NUMBER_OF_FRIENDS_ON_USER_PAGE = "//*[@id=\"wide_column\"]/div[1]/div[2]/a[2]/div[1]";


        try {
            //Открываем страницу со списком друзей пользователя
            String userFriendsLink = "//*[@id=\"profile_friends\"]/a[2]/div/span[1]";
            waitLoadElementExp(userFriendsLink);
            driver.findElement(By.xpath(userFriendsLink)).click();

            try {
                //Запускаем логику, которая соберет ссылки на страницы друзей
                ArrayList<String> listFriends = new ArrayList<>();
                try {
                    waitLoadElementExp(NUMBER_OF_FRIENDS_ON_PAGE_LIST_FRIENDS);
                    //Пройдет по открытой странице с друзьями и соберет краткую информацию о них в список
                    int totalNumberOfFriends = Integer.parseInt(driver.findElement(By.xpath(NUMBER_OF_FRIENDS_ON_PAGE_LIST_FRIENDS)).getText());
                    //System.out.println("totalNumber = " + totalNumberOfFriends);
                    int countCurrentNumberOfFriends = 0;
                    int countFriendsBlocks = 1;

                    while (countCurrentNumberOfFriends < totalNumberOfFriends) {
                        try {
                            waitLoadElementExp(FRIEND_LIST_CONT_PART1 + "[" + countFriendsBlocks + "]");
                            //System.out.println("Вошли в " + countFriendsBlocks + "-ый блок друзей");
                            for (int i = 1; i <= numberFriendsOnBox && countCurrentNumberOfFriends < totalNumberOfFriends; i++) {
                                String currentFriendXPath = FRIEND_LIST_CONT_PART1 + "[" + countFriendsBlocks + "]" + FRIEND_LIST_ELEMENT_FULL_USER_INFO_PART2 + "[" + i + "]" + FRIEND_LIST_ELEMENT_FIELD_TITLE_PART3;
                                try {
                                    waitLoadElementExp(currentFriendXPath);
                                    WebElement currentFriend = driver.findElement(By.xpath(currentFriendXPath));
                                    //String name = currentFriend.getText();
                                    String pageLink = currentFriend.getAttribute("href");
                                    //User tempUser = new User(pageLink, name);
                                    listFriends.add(pageLink);
                                    countCurrentNumberOfFriends++;

                            /*System.out.println("#" + countCurrentNumberOfFriends);
                            tempUser.display();
                            System.out.println("-------------------");*/
                                } catch (LoadException e) {
                                    throw new LoadException("Не удалось найти друга");
                                }
                            }
                            scrollPageToBottom();
                            countFriendsBlocks++;
                        } catch (LoadException e) {
                            throw new LoadException("Не удалось найти блок друзей");
                        }

                    }

                    return listFriends;
                } catch (LoadException e) {
                    throw new LoadException("Не удалось найти общее количество друзей");
                }
            } catch (LoadException e) {
                throw e;
            }
        } catch (LoadException e) {
            throw new LoadException("Не удалось перейти на вкладку 'Друзья'");
        }


    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Все действия, связанные с лайками/репостами
    public void likeSeveralPostsByPage(String pageLink,int totalNumberLikes){
        try {
            openUserPage(pageLink);
            try{
                likeSeveralPostsOnPage(totalNumberLikes);
            }catch (LoadException e){
                e.printStackTrace();
                throw e;
            }
        } catch (LoadException e) {
            throw e;
        }
    }
    public void likeSeveralPostsOnPage(int totalNumberLikes) {
        //Пройдем по странице пользователя и будем лайкать посты
        //При необходимости скроллить страницу

        //Путь к блоку записи
        ////*[@id="page_wall_posts"]//div[contains(@class,'_post post page_block')]
        //*[@id="post226361909_2159"]/div/div[2]/div/div[2]/div

        //Путь к панели с кнопками 'Нравится' под каждым постом
        //*[@id="page_wall_posts"]//div[contains(@class,'_post post page_block')]/div/div[2]/div/div[2]/div

        //Путь конкретно к кнопке 'Мне нравится' под каждым постом
        //*[@id="page_wall_posts"]//div[contains(@class,'_post post page_block')]/div/div[2]/div/div[2]/div//div[contains(@class,'like_btns')]//a[1]

        int countLike = 1;
        int realCountLike = 0;

        while (countLike <= totalNumberLikes) {
            //Поймаем момент, когда нужно пролистнуть страницу
            if (countLike % numberPostsOnBlock == 0) {
                scrollPageToBottom();
            }
            String btnLikePostOnWallXPath = "//*[@id=\"page_wall_posts\"]//div[contains(@class,'_post post page_block')]" + "[" + countLike + "]" +
                    "/div/div[2]/div/div[2]/div//div[contains(@class,'like_btns')]//a[1]";
            try{
                waitLoadElementExp(btnLikePostOnWallXPath);
                //Проверим, стоит ли лайк
                String btnLikePostOnWallActiveXPath = "//*[@id=\"page_wall_posts\"]//div[contains(@class,'_post post page_block')]" + "[" + countLike + "]" +
                        "/div/div[2]/div/div[2]/div//div[contains(@class,'like_btns')]//a[1][contains(@class,'active')]";
                try{
                    //Если удастся, значит лайк стоит
                    waitLoadElementExp(btnLikePostOnWallActiveXPath);
                    System.out.println("Не лайкали, но просмотрели " + countLike + "-ый пост");

                }   catch (LoadException e){
                    //Т.е. пост не лайкали
                    if (shouldPostBeLiked()) {
                        //driver.findElement(By.xpath(btnLikePostOnWallXPath)).click();
                        System.out.println("Лайкнули " + countLike + "-ый пост");
                        realCountLike++;
                    } else {
                        System.out.println("Пропустили " + countLike + "-ый пост, ибо так решила судьба");
                    }
                }

                countLike++;
            }catch (LoadException e){
                throw new LoadException("Не удалось найти запись");
            }
        }
        System.out.println("Итого у пользователя  пролайкали " + realCountLike + " записей");


    }

    public void likeProfilePhotoByPage(String pageLink){
        try {
            openUserPage(pageLink);
            try{
                likeProfilePhotoOnPage();
            }catch (LoadException e){
                e.printStackTrace();
                throw e;
            }
        } catch (LoadException e) {
            throw e;
        }
    }
    public void likeProfilePhotoOnPage() {
        try{
            String profilePhotoXPath = "//*[@id=\"profile_photo_link\"]/img";
            waitLoadElementExp(profilePhotoXPath);
            WebElement profilePhoto = driver.findElement(By.xpath(profilePhotoXPath));
            profilePhoto.click();

            try{
                //Ждем пока прогрузится фотография профиля
                waitLoadElementByTypeExp(ConstVK.PHOTO_POST);
                //Сначала проверим наличие собственного лайка
                if (wasCurrentPostLiked(ConstVK.PHOTO_POST)) {
                    System.out.println("Отметка 'Мне нравится' уже стоит");
                    System.out.println("Result: false");
                    System.out.println(separator);
                } else {
                    System.out.println("Отметка 'Мне нравится' не стоит. Поставим ее");
                    //Нажимаем кнопку 'Мне нравится'
                    likeCurrentPost(ConstVK.PHOTO_POST, ConstVK.LIKE);
                    /*WebElement btnLike = driver.findElement(By.xpath(btnLikeXPath));
                    btnLike.click();*/
                    System.out.println("Result: true");
                    System.out.println(separator);
                }
            }catch (LoadException e){
                throw new LoadException("Фотография недоступна");
            }

        }catch (LoadException e){
            throw e;
        }



    }

    private boolean wasCurrentPostLiked(ConstVK typeOfPost) throws WebDriverException {
        //Активная кнопка 'Мне нравится'
        String BTN_LIKE_ACTIVE_WL_POST_XPATH = "//*[@id=\"wl_post_actions_wrap\"]/div/div/div[1]/a[contains(@class,'like active')][1]";
        String BTN_LIKE_ACTIVE_PHOTO_XPATH = "//*[@id=\"pv_narrow\"]/div[1]/div[1]/div/div/div[1]/div[3]/div/div[1]//a[contains(@class,'like active')]";

        //Панель кнопок 'Нравится' и 'Поделиться' для поста на стене
        String BTNS_LIKE_SHARE_PANEL_WL_POST_XPATH = "//*[@id=\"wl_post_actions_wrap\"]";
        String BTNS_LIKE_SHARE_PANEL_PHOTO_XPATH = "//*[@id=\"pv_narrow\"]/div[1]/div[1]/div/div/div[1]/div[3]/div/div[1]";


        String btnsLikeAndShareXPath = new String();    //панель кнопок 'Нравится' и 'Поделиться'
        String btnLikeActiveXPath = new String();       //xpath для активной, т.е. нажатой кнопки 'Нравится'
        switch (typeOfPost) {
            case WL_POST:
                btnsLikeAndShareXPath = BTNS_LIKE_SHARE_PANEL_WL_POST_XPATH;
                btnLikeActiveXPath = BTN_LIKE_ACTIVE_WL_POST_XPATH;
                break;
            case PHOTO_POST:
                btnsLikeAndShareXPath = BTNS_LIKE_SHARE_PANEL_PHOTO_XPATH;
                btnLikeActiveXPath = BTN_LIKE_ACTIVE_PHOTO_XPATH;
                break;
        }
        /*//Ждем, пока прогрузится панель кнопок 'Нравится' и 'Поделиться'
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(btnsLikeAndShareXPath)));*/
        //Проверяем, отрисовывается ли класс 'like active'
        try {
            return driver.findElement(By.xpath(btnLikeActiveXPath)).isDisplayed();
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }

    }

/////////////////////////////////////////////////////////////

    public boolean likePostByLink(String linkPost, ConstVK typeOfPost, ConstVK typeOfAction) {
        driver.get(linkPost);
        return likeCurrentPost(typeOfPost, typeOfAction);
    }
    public boolean likeCurrentPost(ConstVK typeOfPost, ConstVK typeOfAction) {
        //Кнопка 'Мне нравится'
        String BTN_LIKE_WL_POST_XPATH = "//*[@id=\"wl_post_actions_wrap\"]/div/div/div[1]/a[1]";
        String BTN_LIKE_PHOTO_XPATH = "//*[@id=\"pv_narrow\"]/div[1]/div[1]/div/div/div[1]/div[3]/div/div[1]/a[1]";


        //Панель кнопок 'Нравится' и 'Поделиться' для поста на стене
        String BTNS_LIKE_SHARE_PANEL_WL_POST_XPATH = "//*[@id=\"wl_post_actions_wrap\"]";
        String BTNS_LIKE_SHARE_PANEL_PHOTO_XPATH = "//*[@id=\"pv_narrow\"]/div[1]/div[1]/div/div/div[1]/div[3]/div/div[1]";
        if (waitLoadOfElementByTypeOfElementXPath(typeOfPost)) {
            //Страница прогружена, можем без лишних проверок с ней работать
            System.out.println("=============================================");
            System.out.println("Пост " + typeOfPost + " прогружен");


            String btnsLikeAndShareXPath = new String();    //панель кнопок 'Нравится' и 'Поделиться'
            String btnLikeXPath = new String();            //xpath для кнопки 'Нравится'
            switch (typeOfPost) {
                case WL_POST:
                    btnsLikeAndShareXPath = BTNS_LIKE_SHARE_PANEL_WL_POST_XPATH;
                    btnLikeXPath = BTN_LIKE_WL_POST_XPATH;
                    break;
                case PHOTO_POST:
                    btnsLikeAndShareXPath = BTNS_LIKE_SHARE_PANEL_PHOTO_XPATH;
                    btnLikeXPath = BTN_LIKE_PHOTO_XPATH;
                    break;
            }

            /*wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(btnsLikeAndShareXPath)));
            System.out.println("Пост загрузился");*/

            //Определяем действие с записью по typeOfAction. Ставим лайк или убираем его
            switch (typeOfAction) {
                case LIKE:
                    //Сначала проверим наличие собственного лайка
                    if (!wasCurrentPostLiked(typeOfPost)) {
                        System.out.println("Отметка 'Мне нравится' не стоит. Поставим ее");
                        /*//Ждем, пока кнопка 'Мне нравится' прогрузится и нажимаем ее
                        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(btnLikeXPath)));*/

                        WebElement btnLike = driver.findElement(By.xpath(btnLikeXPath));
                        btnLike.click();
                        System.out.println("Result: true");
                        System.out.println("=============================================");
                        return true;
                    } else {
                        System.out.println("Result: false");
                        System.out.println("=============================================");
                        return false;
                    }
                case DISLIKE:
                    if (wasCurrentPostLiked(typeOfPost)) {
                        System.out.println("Отметка 'Мне нравится' стоит. Уберем ее");
                        /*//Ждем, пока кнопка 'Мне нравится' прогрузится и нажимаем ее
                        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(btnLikeXPath)));*/

                        WebElement btnLike = driver.findElement(By.xpath(btnLikeXPath));
                        btnLike.click();
                        System.out.println("Result: true");
                        System.out.println("=============================================");
                        return true;
                    } else {
                        System.out.println("Result: false");
                        System.out.println("=============================================");
                        return false;
                    }
            }
            return false;
        } else {
            return false;
        }
    }

    public boolean likeSeveralPhotos(String pageLink, int numberPhotosForLike) {
        String PHOTO_GALLERY_XPATH = "//*[@id=\"page_photos_module\"]/a[1]";

        System.out.println(separator);
        System.out.println("Start: likeAllPhotos");

        driver.get(pageLink);
        if (waitLoadOfElementByTypeOfElementXPath(ConstVK.USER_PAGE)) {
            if (waitLoadOfElementByXPath(PHOTO_GALLERY_XPATH)) {
                //Нажали на первое фото в галерее
                WebElement firstPhoto = driver.findElement(By.xpath(PHOTO_GALLERY_XPATH));
                firstPhoto.click();

                //Подождали, пока он прогрузится
                waitLoadOfElementByTypeOfElementXPath(ConstVK.PHOTO_POST);
                //Лайкнем его или уберем лайк для наглядности работы
                if (wasCurrentPostLiked(ConstVK.PHOTO_POST)) {
                    likeCurrentPost(ConstVK.PHOTO_POST, ConstVK.DISLIKE);
                } else {
                    likeCurrentPost(ConstVK.PHOTO_POST, ConstVK.LIKE);
                }

                for (int i = 0; i < numberPhotosForLike; i++) {
                    sleep(10);
                    //Нажмем на следующее фото и зациклим
                    if (getNextPhoto(ConstVK.RIGHT)) {
                        waitLoadOfElementByTypeOfElementXPath(ConstVK.PHOTO_POST);
                        System.out.println("Дождались загрузки следующей фотографии");
                        //Лайкнем его или уберем лайк для наглядности работы
                        if (wasCurrentPostLiked(ConstVK.PHOTO_POST)) {
                            likeCurrentPost(ConstVK.PHOTO_POST, ConstVK.DISLIKE);
                        } else {
                            likeCurrentPost(ConstVK.PHOTO_POST, ConstVK.LIKE);
                        }
                    } else {
                        System.out.println("Не удалось открыть следующее фото");
                        System.out.println(separator);
                        return false;
                    }
                }
                System.out.println("Finish: likeAllPhotos");
                System.out.println(separator);
                return true;
            } else {
                System.out.println("Не удалось загрузить галерею фотографий");
                System.out.println(separator);
                return false;
            }
        } else {
            System.out.println("Не удалось загрузить страницу пользователя");
            System.out.println(separator);
            return false;
        }
    }
    public boolean likeSeveralPosts(String startPostLink, int numberPostsForLike) {
        System.out.println(separator);
        System.out.println("Start: likeSeveralPosts");

        driver.get(startPostLink);

        if (waitLoadOfElementByTypeOfElementXPath(ConstVK.WL_POST)) {
            //System.out.println("Пост загрузился");

            for (int i = 0; i < numberPostsForLike; i++) {
                //Торможение, чтобы 'действия не казались подозрительными'
                if (i % safetyNumberLikes == 0) {
                    sleep(10);
                }
                if (!wasCurrentPostLiked(ConstVK.WL_POST)) {
                    likeCurrentPost(ConstVK.WL_POST, ConstVK.LIKE);
                }
                getNextWlPost(ConstVK.RIGHT);
            }


            System.out.println("Result likeSeveralPosts: true");
            System.out.println(separator);
            return true;
        } else {
            System.out.println("Не удалось загрузить пост");
            System.out.println(separator);
            return false;
        }
    }

    public boolean getNextWlPost(ConstVK direction) {
        //Кнопки 'влево' и 'вправо' для записей на стене
        String BTN_NAV_WL_POST_RIGHT_XPATH = "//*[@id=\"wk_right_arrow\"]";
        String BTN_NAV_WL_POST_LEFT_XPATH = "//*[@id=\"wk_left_arrow\"]";

        String btnNavWlPost = new String();
        switch (direction) {
            case LEFT:
                btnNavWlPost = BTN_NAV_WL_POST_LEFT_XPATH;
                break;
            case RIGHT:
                btnNavWlPost = BTN_NAV_WL_POST_RIGHT_XPATH;
                break;
        }
        if (waitLoadOfElementByXPath(btnNavWlPost)) {
            WebElement btnNav = driver.findElement(By.xpath(btnNavWlPost));
            btnNav.click();
            return true;
        } else {
            System.out.println("Не удалось найти кнопку навигации");
            System.out.println(separator);
            return false;
        }
    }
    public boolean getNextPhoto(ConstVK direction) {
        //Область фотографии, чтобы появились кнопки навигации влево и вправо
        String AREA_PHOTO_XPATH = "//*[@id=\"pv_box\"]/div[2]/div[2]/div[1]/div[6]";
        //Кнопки 'влево' и 'вправо' в галерее фотографий
        String BTN_NAV_ICON_PHOTO_LEFT_XPATH = "//*[@id=\"pv_nav_btn_left\"]/div";
        String BTN_NAV_ICON_PHOTO_RIGHT_XPATH = "//*[@id=\"pv_nav_btn_right\"]/div";
        //String BTN_NAV_SHOW_PHOTO_LEFT_XPATH = "//*[@id=\"pv_box\"]/div[2]/div[2]/div[1]/div[6]//div[contains(@class,'pv_nav_btn_show')]";


        String btnNavIconPhotoXPath = new String();
        switch (direction) {
            case LEFT:
                btnNavIconPhotoXPath = BTN_NAV_ICON_PHOTO_LEFT_XPATH;
                break;
            case RIGHT:
                btnNavIconPhotoXPath = BTN_NAV_ICON_PHOTO_RIGHT_XPATH;
                break;
        }
        if (waitLoadOfElementByXPath(AREA_PHOTO_XPATH)) {
            WebElement element = driver.findElement(By.xpath(AREA_PHOTO_XPATH));
            Actions actions = new Actions(driver);
            actions.moveToElement(element).perform();
            sleep(2);

            //String btnLeftIconXPath = "//*[@id=\"pv_nav_btn_left\"]/div";
            if (waitLoadOfElementByXPath(btnNavIconPhotoXPath)) {
                WebElement btnLeft = driver.findElement(By.xpath(btnNavIconPhotoXPath));
                actions.click(btnLeft).perform();
                //btnLeft.click();
                System.out.println("Нажали кнопку навигации");
                System.out.println(separator);
                return true;
            } else {
                System.out.println("Не удалось отобразить кнопку навигации");
                System.out.println(separator);
                return false;
            }
            //wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(BTN_NAV_ICON_PHOTO_LEFT_XPATH)));
        } else {
            System.out.println("Не удалось найти PHOTO_AREA_FOR_SHOW_NAV_BTN_XPATH");
            System.out.println(separator);
            return false;
        }

    }

    public String getLinkToFirstTextPost(String pageLink) {
        //Пройдем по странице, пока не найдем первый пост с текстовой частью
        //Откроем его и вернем ссылку
        //Такой способ пролистывать посты быстрее, а главное уже написан

        //XPath до текстового поля поста
        System.out.println(separator);
        System.out.println("Start: getLinkToFirstPost");

        driver.get(pageLink);
        if (waitLoadOfElementByTypeOfElementXPath(ConstVK.USER_PAGE)) {
            String WL_POST_TEXT = "//*[@id=\"page_wall_posts\"]//div[contains(@class,'_post')]" +
                    "//div//div[contains(@class,'post_content')]//div//div[contains(@class,'wall_text')]//div//div[contains(@class,'wall_post_text')]";
            if (waitLoadOfElementByXPath(WL_POST_TEXT)) {
                driver.findElement(By.xpath(WL_POST_TEXT)).click();
                sleep(2);
                if (waitLoadOfElementByTypeOfElementXPath(ConstVK.WL_POST)) {
                    return driver.getCurrentUrl();
                }
            }
        } else {
            System.out.println("Не удалось загрузить страницу пользователя");
        }


        return null;
    }
    public String getLinkToFirstPost(String pageLink) {
        //Пока отработано только для поста с текстом
        String firstPostLink = "";
        System.out.println(separator);
        System.out.println("Start: getLinkToFirstPost");

        driver.get(pageLink);
        if (waitLoadOfElementByTypeOfElementXPath(ConstVK.USER_PAGE)) {
            String FIRST_WL_POST_XPATH = "//*[@id=\"page_wall_posts\"]//div[contains(@class,'_post post page_block')][1]" +
                    "//div[contains(@class,'wall_post_text')]";
            //*[@id=\"page_wall_posts\"]//div[contains(@class,'_post post page_block')][1]//div[contains(@class,'wall_post_cont')]";

            if (waitLoadOfElementByXPath(FIRST_WL_POST_XPATH)) {
                driver.findElement(By.xpath(FIRST_WL_POST_XPATH)).click();
                sleep(2);
                firstPostLink = driver.getCurrentUrl();
                System.out.println(firstPostLink);

                System.out.println("Result getLinkToFirstPost: true");
                System.out.println(firstPostLink);
                System.out.println(separator);
                return firstPostLink;
            } else {
                System.out.println("Не удалось найти пост");
                System.out.println(separator);
                return null;
            }
        } else {
            System.out.println("Не удалось открыть страницу пользователя");
            System.out.println(separator);
            return null;
        }

    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    //Вспомогательные методы
    public void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            System.out.println("----------------------");
            System.out.println("Ошибка в методе sleep");
            System.out.println(e.getMessage());
            System.out.println("----------------------");
        }
    }

    protected boolean waitLoadOfElementByXPath(String xpath) {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
            return true;
        } catch (WebDriverException e) {
            System.out.println(separator);
            System.out.println("Элемент byXPath не был загружен");
            System.out.println(e.getMessage());
            System.out.println(separator);
            return false;
        }
    }

    public void waitLoadElementExp(String xpath) throws LoadException {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
        } catch (WebDriverException e) {
            throw new LoadException("Не удалось загрузить элемент");
        }
    }

    protected void waitLoadElementByTypeExp(ConstVK typeOfElement) throws LoadException {
        String elementXPath = new String();
        switch (typeOfElement) {
            case WL_POST:
                //Панель кнопок 'Нравится' и 'Поделиться' для поста на стене
                String BTNS_LIKE_SHARE_PANEL_WL_POST_XPATH = "//*[@id=\"wl_post_actions_wrap\"]";
                elementXPath = BTNS_LIKE_SHARE_PANEL_WL_POST_XPATH;
                break;
            case PHOTO_POST:
                String BTNS_LIKE_SHARE_PANEL_PHOTO_XPATH = "//*[@id=\"pv_narrow\"]/div[1]/div[1]/div/div/div[1]/div[3]/div/div[1]";
                elementXPath = BTNS_LIKE_SHARE_PANEL_PHOTO_XPATH;
                break;
            case USER_PAGE:
                String PROFILE_NAME_XPATH = "//*[@id=\"page_info_wrap\"]/div[1]/h2";
                elementXPath = PROFILE_NAME_XPATH;
                break;
            case WELCOME_PAGE:
                String LOGIN_PANEL_XPATH = "//*[@id=\"index_login_form\"]";   //Вся панель авторизации
                elementXPath = LOGIN_PANEL_XPATH;
                break;
            case HOST_USER_PAGE:
                String TOP_PROFILE_LINK = "//*[@id=\"top_profile_link\"]";    //Ссылка на страницу хозяина. Для проверки входа
                elementXPath = TOP_PROFILE_LINK;
                break;

        }
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(elementXPath)));
        } catch (WebDriverException e) {
            throw new LoadException("Не удалось загрузить элемент: " + typeOfElement);
        }
    }

    protected boolean waitLoadOfElementByTypeOfElementXPath(ConstVK typeOfElement) {
        //Панель кнопок 'Нравится' и 'Поделиться' для поста на стене
        String BTNS_LIKE_SHARE_PANEL_WL_POST_XPATH = "//*[@id=\"wl_post_actions_wrap\"]";
        String BTNS_LIKE_SHARE_PANEL_PHOTO_XPATH = "//*[@id=\"pv_narrow\"]/div[1]/div[1]/div/div/div[1]/div[3]/div/div[1]";

        String elementXPath = new String();
        switch (typeOfElement) {
            case WL_POST:
                elementXPath = BTNS_LIKE_SHARE_PANEL_WL_POST_XPATH;
                break;
            case PHOTO_POST:
                elementXPath = BTNS_LIKE_SHARE_PANEL_PHOTO_XPATH;
                break;
            case USER_PAGE:
                String PROFILE_NAME_XPATH = "//*[@id=\"page_info_wrap\"]/div[1]/h2";
                elementXPath = PROFILE_NAME_XPATH;
                break;
            case WELCOME_PAGE:
                String LOGIN_PANEL_XPATH = "//*[@id=\"index_login_form\"]";   //Вся панель авторизации
                elementXPath = LOGIN_PANEL_XPATH;
                break;
            case HOST_USER_PAGE:
                String TOP_PROFILE_LINK = "//*[@id=\"top_profile_link\"]";    //Ссылка на страницу хозяина. Для проверки входа
                elementXPath = TOP_PROFILE_LINK;
                break;

        }
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(elementXPath)));
            return true;
        } catch (WebDriverException e) {
            System.out.println(separator);
            System.out.println("Элемент " + typeOfElement + " не был загружен");
            System.out.println(e.getMessage());
            System.out.println(separator);
            return false;
        }
    }

    public void scrollPageToBottom() {
        JavascriptExecutor javascript = (JavascriptExecutor) driver;
        javascript.executeScript("window.scrollTo(0, document.body.scrollHeight)", "");
    }

    private boolean shouldPostBeLiked() {
        Random rand = new Random();
        int coinToss = rand.nextInt(2);
        if (coinToss == 0) {
            return true;
        } else return false;
    }

    public void openUserPage(String pageLink) throws LoadException {
        driver.get(pageLink);
        try {
            waitLoadElementByTypeExp(ConstVK.USER_PAGE);
        } catch (LoadException e) {
            throw new LoadException("openUserPage: не удалось загрузить страницу пользователя");
        }
    }


    //Методы по заполнению полей User'а
    public User getStartInfoUserByPage(String profileLink) throws SearchIDException, LoadException {
        //Получим минимальную необходимую информацию о пользователе: ID, name, link
        driver.get(profileLink);
        try {
            waitLoadElementByTypeExp(ConstVK.USER_PAGE);
            try{
                return getStartInfoUserOnPage(profileLink);
            }catch (LoadException e){
                throw e;
            }
        } catch (LoadException e) {
            throw new LoadException("Не удалось загрузить страницу пользователя");
        }
    }
    public User getStartInfoUserOnPage(String profileLink) throws LoadException, SearchIDException {
        try {
            int profileID = getDefaultIDUserOnPage();
            String pageName = getUserNameOnPage();

            User currentUser = new User(profileID, profileLink, pageName);
            return currentUser;

        } catch (LoadException e) {
            throw new LoadException("Не получить минимальную необходимую информацию о пользователе");
        } catch (SearchIDException e) {
            throw new SearchIDException("Не удалось получить ID");
        }
    }

    public User getFullInfoFromUserByPage(String pageLink) throws LoadException {
        try {
            openUserPage(pageLink);
            try{
                return getFullInfoFromUserOnPage(pageLink);
            }catch (LoadException e){
                throw e;
            }
        } catch (LoadException e) {
            throw new LoadException("getFullInfoFromUserByPage: не удалось загрузить страницу пользователя");
        }
    }
    public User getFullInfoFromUserOnPage(String pageLink) throws LoadException {
        try {
            User temp = getStartInfoUserByPage(pageLink);
            try {
                temp.setHostProfileLink(getHostPageLink());
            } catch (LoadException e) {
                //throw new LoadException("getFullInfoFromUserOnPage: не удалось получить hostPageLink");
                e.printStackTrace();
            }
            try {
                temp.setDateBirth(getUserDateBirthOnPage());
                //temp.setAge(temp.calculateAge());         //Делается на стороне пользователя в методе setDateBirh
            } catch (LoadException e) {
                //throw new LoadException("getFullInfoFromUserOnPage: не удалось получить dateBirthday");
                e.printStackTrace();
            }
            try {
                temp.setCity(getUserCityOnPage());
            } catch (LoadException e) {
                //throw new LoadException("getFullInfoFromUserOnPage: не удалось получить city");
                e.printStackTrace();
            }
            try {
                temp.setStatusFriend(getFriendStatusOnPage());
            } catch (LoadException e) {
                //throw new LoadException("getFullInfoFromUserOnPage: не удалось получить StatusRequestAnswer");
                e.printStackTrace();
            }
            try {
                temp.setCountFriends(getCountInfoUserOnPage(ConstVK.COUNT_ALL_FRIENDS));
            } catch (LoadException e) {
                //throw new LoadException("getFullInfoFromUserOnPage: не удалось получить CountAllFriends");
                e.printStackTrace();
            }
            try {
                temp.setCountCommonFriends(getCountInfoUserOnPage(ConstVK.COUNT_COMMON_FRIENDS));
            } catch (LoadException e) {
                //throw new LoadException("getFullInfoFromUserOnPage: не удалось получить CountCommonFriends");
                e.printStackTrace();
            }
            try {
                temp.setCountFollowers(getCountInfoUserOnPage(ConstVK.COUNT_FOLLOWERS));
            } catch (LoadException e) {
                //throw new LoadException("getFullInfoFromUserOnPage: не удалось получить CountFollowers");
                e.printStackTrace();
            }

            SimpleDateFormat formatForDateNow = new SimpleDateFormat("YYYY-MM-dd");
            String currentDate = formatForDateNow.format(new Date());
            temp.setDateRequest(currentDate);

            return temp;
        } catch (LoadException e) {
            e.printStackTrace();
            throw new LoadException("getFullInfoFromUserOnPage:LoadException: не удалось получить необходитую минимальную информацию о пользователе");
        } catch (SearchIDException e) {
            throw new LoadException("getFullInfoFromUserOnPage:SearchIDException: не удалось получить необходитую минимальную информацию о пользователе");
        }
    }

    //Получаем имя пользователя
    public String getUserNameByPage(String pageLink) throws LoadException {
        try {
            openUserPage(pageLink);
            try{
                return getUserNameOnPage();
            }catch (LoadException e){
                throw e;
            }
        } catch (LoadException e) {
            throw new LoadException("Не удалось загрузить страницу пользователя");
        }
    }
    public String getUserNameOnPage() throws LoadException {
        try {
            String PROFILE_NAME_XPATH = "//*[@id=\"page_info_wrap\"]/div[1]/h2";
            return driver.findElement(By.xpath(PROFILE_NAME_XPATH)).getText();
        } catch (LoadException e) {
            throw new LoadException("Не удалось получить имя пользователя");
        }
    }

    //Получаем исходный ID пользователя
    public int getDefaultIDUserByPage(String pageLink) throws LoadException, SearchIDException {
        try {
            openUserPage(pageLink);
            try{
                return getDefaultIDUserOnPage();
            }catch (LoadException e){
                throw e;
            }
        } catch (LoadException e) {
            throw new LoadException("Не удалось загрузить страницу пользователя");
        }
    }
    public int getDefaultIDUserOnPage() throws LoadException, SearchIDException {
        try {
            String wallXpath = "//*[@id=\"wall_tabs\"]/li[1]/a";
            waitLoadElementExp(wallXpath);

            String forProfileID = driver.findElement(By.xpath(wallXpath)).getAttribute("href");
            System.out.println(forProfileID);

            try {
                return hp.getDefaultIDByWall(forProfileID);
            } catch (SearchIDException e) {
                throw new SearchIDException("Не удалось получить ID");
            }

        } catch (LoadException e) {
            throw new LoadException("Не получить ID пользователя");
        }

    }

    //Получаем текущий город пользователя
    public String getUserCityByPage(String pageLink) throws LoadException {
        driver.get(pageLink);
        try {
            waitLoadElementByTypeExp(ConstVK.USER_PAGE);
            try{
                return getUserCityOnPage();
            }catch (LoadException e){
                throw e;
            }
        } catch (LoadException e) {
            throw new LoadException("getUserCityByPage: не удалось загрузить страницу пользователя");
        }
    }
    public String getUserCityOnPage() throws LoadException {
        try {
            String cityXPath = "//*[@id=\"profile_short\"]/div/div[contains(@class,'labeled')]/a[contains(@href,'city')]";
            waitLoadElementExp(cityXPath);
            return driver.findElement(By.xpath(cityXPath)).getText();
        } catch (LoadException e) {
            throw new LoadException("Нет информации о городе");
        }
    }

    //Получаем дату рождения пользователя
    public String getUserDateBirthByPage(String pageLink) throws LoadException {
        driver.get(pageLink);
        try {
            waitLoadElementByTypeExp(ConstVK.USER_PAGE);
            try{
                return getUserDateBirthOnPage();
            }catch (LoadException e){
                throw e;
            }
        } catch (LoadException e) {
            throw new LoadException("getUserDateBirthByPage: не удалось загрузить страницу пользователя");
        }
    }
    public String getUserDateBirthOnPage() throws LoadException {
        //Получим дату рождения
        String dayAndMonthLinkXPath = "//*[@id=\"profile_short\"]/div[1]/div[2]/a[1]";
        String yearLinkXPath = "//*[@id=\"profile_short\"]/div[1]/div[2]/a[2]";

        String dateOfBirth = "0000-00-00";

        try {
            waitLoadElementExp(yearLinkXPath);
            String byearLink = driver.findElement(By.xpath(yearLinkXPath)).getAttribute("href");
            //int byear = hp.getBDigit(byearLink, ConstVK.BYEAR);
            dateOfBirth = hp.getYearStr(byearLink);
        } catch (LoadException e) {
            dateOfBirth = "0000";
        }
        try {
            waitLoadElementExp(dayAndMonthLinkXPath);
            String bdayAndMonthLink = driver.findElement(By.xpath(dayAndMonthLinkXPath)).getAttribute("href");
            dateOfBirth += hp.getDayAndMonthStr(bdayAndMonthLink);

            /*int bday = hp.getBDigit(bdayAndMonthLink, ConstVK.BDAY);
            int bmonth = hp.getBDigit(bdayAndMonthLink, ConstVK.BMONTH);*/
        } catch (LoadException e) {
            dateOfBirth += "-00-00";
            //throw new LoadException("Не удалось загрузить ссылку для дня и месяца рождения");
        }
        return dateOfBirth;

    }

    //Получаем статус заявки в друзья пользователя
    public int getFriendStatusByPage(String pageLink) throws LoadException {
        try {
            openUserPage(pageLink);
            try {
                return getFriendStatusOnPage();
            } catch (LoadException e) {
                throw e;
            }
        } catch (LoadException e) {
            throw new LoadException("getFriendStatusByPage: не удалось загрузить страницу пользователя");
        }
    }
    public int getFriendStatusOnPage() throws LoadException {
        /*
         * -2 - отлонил заявку
         * -1 - не ответил на заявку
         *  0 - не отправляли заявку
         *  1 - друг
         * */
        try {
            String btnAddToFriend = "//*[@id=\"friend_status\"]/div/button";
            waitLoadElementExp(btnAddToFriend);
            //Кнопка 'Добавить в друзья' активна, возвращаем 0
            return 0;
        } catch (LoadException e) {
            //Кнопка 'Добавить в друзья' не активна
            try {
                String btnActionsWithFriend = "//*[@id=\"friend_status\"]/div[contains(@class,'flat_button button_wide secondary page_actions_btn')]/span";
                waitLoadElementExp(btnActionsWithFriend);
                String msg = driver.findElement(By.xpath(btnActionsWithFriend)).getText();
                //System.out.println("getFriendStatusOnPage: msg from button: " + msg);
                if (msg.equalsIgnoreCase("У Вас в друзьях") | msg.equalsIgnoreCase("In your friend list")) {
                    return 1;
                } else {
                    if (msg.equalsIgnoreCase("Заявка отправлена") | msg.equalsIgnoreCase("Request sent")) {
                        return -1;
                    } else {
                        return -2;
                    }
                }
            } catch (LoadException ee) {
                throw new LoadException("Не удалось проверить статус");
            }
        }
    }

    //Получаем информацию и количестве друзей/подписчиков
    public int getCountInfoUserByPage(String pageLink, ConstVK typeCount) throws LoadException {
        driver.get(pageLink);
        try {
            waitLoadElementByTypeExp(ConstVK.USER_PAGE);
            try{
                return getCountInfoUserOnPage(typeCount);
            }catch (LoadException e){
                throw e;
            }
        } catch (LoadException e) {
            throw new LoadException("getCountInfoUserByPage: не удалось загрузить страницу");
        }

    }
    private int getCountInfoUserOnPage(ConstVK typeCount) throws LoadException{
        //Вызывается со страницы пользрвателя
        int result = 0;
        //Панель со всеми счетчиками страницы: кол-во друзей, подписчиков, общих друзей
        String COUNTS_MODULE = "//*[@id=\"wide_column\"]/div[1]/div[2]";
        String COUNT_COMMON_FRIENDS = "//*[@id=\"wide_column\"]/div[1]/div[2]//a[contains(@href,'common')]//div[contains(@class,'count')]";
        String COUNT_ALL_FRIENDS = "//*[@id=\"wide_column\"]/div[1]/div[2]//a[contains(@href,'all')]//div[contains(@class,'count')]";
        String COUNT_FOLLOWERS = "//*[@id=\"wide_column\"]/div[1]/div[2]//a[contains(@href,'#')]//div[contains(@class,'count')]";

        try {
            waitLoadElementExp(COUNTS_MODULE);
            //Загрузили панель счетчиков страницы
            String currentCountXPath = "";
            switch (typeCount) {
                case COUNT_COMMON_FRIENDS:
                    currentCountXPath = COUNT_COMMON_FRIENDS;
                    break;
                case COUNT_ALL_FRIENDS:
                    currentCountXPath = COUNT_ALL_FRIENDS;
                    break;
                case COUNT_FOLLOWERS:
                    currentCountXPath = COUNT_FOLLOWERS;
                    break;
            }
            try {
                waitLoadElementExp(currentCountXPath);
                //Если такой счетчик существует
                String forResult = driver.findElement(By.xpath(currentCountXPath)).getText();
                //System.out.println(typeCount + " = "+forResult);
                try {
                    forResult = forResult.replaceAll("\\s+","");
                    result = Integer.parseInt(forResult);
                } catch (NumberFormatException e) {
                    System.out.println("Ошибка преобразования  '" + forResult + "' в int");
                }
            }catch (LoadException ee){
                throw new LoadException("Информации о \" + typeCount + \" на странице не найдено");
            }
        }catch (LoadException e){
            //throw new LoadException("Не удалось загрузить панель счетчиков количества друзей пользователя");
        }

        return result;

    }


    //Добавить в друзья
    public void addUserToFriendList(String pageLink) throws LoadException {
        driver.get(pageLink);
        try {
            //Подождем, пока страница пользователя прогрузится
            waitLoadElementByTypeExp(ConstVK.USER_PAGE);

            //Добавим в друзья и занесем в бд
            try {
                String btnAddToFriendsXPath = "//*[@id=\"friend_status\"]/div/button";
                waitLoadElementExp(btnAddToFriendsXPath);
                driver.findElement(By.xpath(btnAddToFriendsXPath)).click();
                sleep(2);
            } catch (LoadException e) {
                //Не прогрузилась кнопка добавить в друзья
                System.out.println(e.getMessage());
                throw new LoadException("Не удалось добавить в друзья");
            }
        } catch (LoadException e) {
            //Не удалось загрузить страницу пользователя
            System.out.println(e.getMessage());
            throw new LoadException("Не удалось добавить в друзья");
        }
    }

    //Написать сообщение
    public void writeMessageByPage(String pageLink, String msg) throws LoadException {
        driver.get(pageLink);
        try {
            waitLoadElementByTypeExp(ConstVK.USER_PAGE);
            writeMessageOnPage(msg);
        } catch (LoadException e) {
            throw new LoadException("Не удалось загрузить страницу ");
        }
    }
    public void writeMessageOnPage(String msg) throws LoadException {
        try {
            String btnWriteMsg = "//*[@id=\"profile_message_send\"]/div/a[1]/button";
            waitLoadElementExp(btnWriteMsg);
            driver.findElement(By.xpath(btnWriteMsg)).click();

            try {
                String textArea = "//*[@id=\"mail_box_editable\"]";
                waitLoadElementExp(textArea);

                sleep(2);
                driver.findElement(By.xpath(textArea)).sendKeys(msg);
                sleep(2);
                driver.findElement(By.xpath("//*[@id=\"mail_box_send\"]")).click();
            } catch (LoadException e) {
                throw new LoadException("Не загрузилось окно отправки сообщения");
            }
        } catch (LoadException e) {
            throw new LoadException("Не удалось написать сообщение");
        }
    }

    //Получить список ссылок на страницы участников группы
    public ArrayList<String> getListGroupMembers(String groupLink, int numberMembersTotal) throws LoadException {
        driver.get(groupLink);
        try {
            String membersXpath = "//*[@id=\"public_followers\"]/a/div/span[1]";    //XPath кнопки для получения списка участников группы
            waitLoadElementExp(membersXpath);
            driver.findElement(By.xpath(membersXpath)).click();
            try {
                //Получим общее число подписчиков
                try{
                    int countGroupMembers = getCountGroupMembersOnOpenMembersPage();
                    //Сравним с запрашиваемым числом
                    if(countGroupMembers<numberMembersTotal){
                        throw new LoadException("В группе недостаточное количество участников");
                    }
                    //Добавим подписчиков в список
                    String memberLinkXPath = "//*[@id=\"fans_rowsmembers\"]/div[contains(@class,'fans_fan_row')]/div[contains(@class,'fans_fan_name')]/a";
                    waitLoadElementExp(memberLinkXPath);

                    //Будем прокручивать лист участников, пока не получим достаточное количество
                    List<WebElement> listMembersWebEl = driver.findElements(By.xpath(memberLinkXPath));
                    ArrayList<String> listMembersLink = new ArrayList<>();

                    while (listMembersWebEl.size() < numberMembersTotal) {
                        try{
                            String btnScroll = "//*[@id=\"fans_more_linkmembers\"]";
                            waitLoadElementExp(btnScroll);
                            driver.findElement(By.xpath(btnScroll)).click();
                            waitLoadElementExp(memberLinkXPath);
                            listMembersWebEl = driver.findElements(By.xpath(memberLinkXPath));
                        }catch(LoadException e) {
                            throw new LoadException("Не удалось пролистнуть страницу для получения списка пользователей");
                        }

                    }
                    System.out.println("Current listMembersWebEl.size = " + listMembersWebEl.size());
                    //Соберем нужное количество ссылок
                    for (int i = 0; i < numberMembersTotal; i++) {
                        //System.out.println("#" + k + " " + listMembersWebEl.get(k).getAttribute("href"));//driver.findElement(By.xpath(memberLinkXPath)).getText());
                        listMembersLink.add((listMembersWebEl.get(i).getAttribute("href")).toString());
                    }

                    /*int k = 0;
                    while (k < numberMembersTotal) {
                        List<WebElement> listMembersWebEl = driver.findElements(By.xpath(memberLinkXPath));
                        int previousNumberMembersOnBlock = listMembersWebEl.size();
                        System.out.println("swGetListLinks: listMembersWebEl.size: "+ listMembersWebEl.size());

                        int numberMembersOnBlock = 60;      //Первоначально отображается 60 подписчиков, затем добавляется новое число, каждый раз разное
                        if (k == numberMembersOnBlock-1) {
                            numberMembersOnBlock = 29;
                        }
                        for (int i = 0; i < numberMembersOnBlock && k < numberMembersTotal; i++) {
                            //System.out.println("#" + k + " " + listMembersWebEl.get(k).getAttribute("href"));//driver.findElement(By.xpath(memberLinkXPath)).getText());
                            listMembersLink.add((listMembersWebEl.get(k).getAttribute("href")).toString());
                            k++;
                        }

                        if(listMembersLink.size()<numberMembersTotal){
                            String btnScroll = "//*[@id=\"fans_more_linkmembers\"]";
                            driver.findElement(By.xpath(btnScroll)).click();
                        }


                    }*/




                    return listMembersLink;
                }catch (LoadException e){
                    throw e;
                }




            } catch (LoadException e) {
                throw new LoadException("Не удалось загрузить участников группы");
            }
        } catch (LoadException e) {
            throw new LoadException("Не удалось загрузить страницу группы");
        }

    }

    public int getCountGroupMembers(String groupLink)throws LoadException{
        driver.get(groupLink);
        try {
            String membersXpath = "//*[@id=\"public_followers\"]/a/div/span[1]";    //XPath кнопки для получения списка участников группы
            waitLoadElementExp(membersXpath);
            driver.findElement(By.xpath(membersXpath)).click();
            try {
                return getCountGroupMembersOnOpenMembersPage();
            } catch (LoadException e) {
                throw e;
            }
        } catch (LoadException e) {
            throw e;
        }
    }
    public int getCountGroupMembersOnOpenMembersPage()throws LoadException {
        try{
            String countMembersXPath = "//*[@id=\"box_layer\"]/div[2]/div/div[2]/div/div[contains(@class,'tb_tabs_wrap')]/div/div/h2/ul/li/div/span";
            waitLoadElementExp(countMembersXPath);

            String countMembersStr = driver.findElement(By.xpath(countMembersXPath)).getText();
            countMembersStr = countMembersStr.replaceAll("\\s","");
            //System.out.println("'"+countMembersStr+"'");
            try {
                return Integer.parseInt(countMembersStr);
            } catch (NumberFormatException e) {
                throw new LoadException("Ошибка преобразования типов");
            }
        }catch (LoadException e){
            throw e;
        }
    }


}

