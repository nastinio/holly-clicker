package ru.nastinio;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.nastinio.Enums.ConstVK;
import ru.nastinio.Exceptions.AddToFriendlistException;
import ru.nastinio.Exceptions.LoadException;
import ru.nastinio.Exceptions.SearchIDException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class SeleniumWorker {

    protected static WebDriver driver;
    protected WebDriverWait wait;

    public HelpFunctionality hp = new HelpFunctionality();

    private final String VK_URL = "https://vk.com/";
    private String hostPageLink;
    //private int hostID;

    //Элементы для авторизации
    protected final String LOGIN_PANEL_XPATH = "//*[@id=\"index_login_form\"]";   //Вся панель авторизации
    protected final String INPUT_LOGIN_XPATH = ".//*[@id='index_email']";         //Поле ввода логина
    protected final String INPUT_PASSWORD_XPATH = ".//*[@id='index_pass']";       //Поле ввода пароля
    protected final String BTN_LOG_XPATH = ".//*[@id='index_login_button']";      //Кнопка войти
    protected final String TOP_PROFILE_LINK = "//*[@id=\"top_profile_link\"]";    //Ссылка на страницу хозяина. Для проверки входа

    //Панель кнопок 'Нравится' и 'Поделиться' для поста на стене
    protected final String BTNS_LIKE_SHARE_PANEL_WL_POST_XPATH = "//*[@id=\"wl_post_actions_wrap\"]";
    protected final String BTNS_LIKE_SHARE_PANEL_PHOTO_XPATH = "//*[@id=\"pv_narrow\"]/div[1]/div[1]/div/div/div[1]/div[3]/div/div[1]";

    //Кнопка 'Мне нравится'
    protected final String BTN_LIKE_WL_POST_XPATH = "//*[@id=\"wl_post_actions_wrap\"]/div/div/div[1]/a[1]";
    protected final String BTN_LIKE_PHOTO_XPATH = "//*[@id=\"pv_narrow\"]/div[1]/div[1]/div/div/div[1]/div[3]/div/div[1]/a[1]";

    //Активная кнопка 'Мне нравится'
    protected final String BTN_LIKE_ACTIVE_WL_POST_XPATH = "//*[@id=\"wl_post_actions_wrap\"]/div/div/div[1]/" +
            "a[contains(@class,'like active')][1]";
    protected final String BTN_LIKE_ACTIVE_PHOTO_XPATH = "//*[@id=\"pv_narrow\"]/div[1]/div[1]/div/div/div[1]/div[3]/div/div[1]//" +
            "a[contains(@class,'like active')]";

    //Вспомогательные элементы страницы
    protected final String PROFILE_NAME_XPATH = "//*[@id=\"page_info_wrap\"]/div[1]/h2";
    protected final String PROFILE_PHOTO_XPATH = "//*[@id=\"profile_photo_link\"]/img";
    protected final String PHOTO_GALLERY_XPATH = "//*[@id=\"page_photos_module\"]/a[1]";

    //Область фотографии, чтобы появились кнопки навигации влево и вправо
    protected final String AREA_PHOTO_XPATH = "//*[@id=\"pv_box\"]/div[2]/div[2]/div[1]/div[6]";
    //Кнопки 'влево' и 'вправо' в галерее фотографий
    protected final String BTN_NAV_ICON_PHOTO_LEFT_XPATH = "//*[@id=\"pv_nav_btn_left\"]/div";
    protected final String BTN_NAV_ICON_PHOTO_RIGHT_XPATH = "//*[@id=\"pv_nav_btn_right\"]/div";
    //String BTN_NAV_SHOW_PHOTO_LEFT_XPATH = "//*[@id=\"pv_box\"]/div[2]/div[2]/div[1]/div[6]//div[contains(@class,'pv_nav_btn_show')]";

    //Кнопки 'влево' и 'вправо' для записей на стене
    protected final String BTN_NAV_WL_POST_RIGHT_XPATH = "//*[@id=\"wk_right_arrow\"]";
    protected final String BTN_NAV_WL_POST_LEFT_XPATH = "//*[@id=\"wk_left_arrow\"]";

    //Локаторы для работы со списком друзей
    protected final String MY_FRIENDS_LINK = "//*[@id=\"l_fr\"]/a/span/span[2]";
    protected final String USER_FRIENDS_LINK = "//*[@id=\"profile_friends\"]/a[2]/div/span[1]";

    // Локаторы для получения информации о друге из укороченного списка
    // По отдельности не используются, т.к. после каждой части неодходимо добавить
    // индекс текущего блока друзей и индекс друга в блоке
    // Полный локатор на примере 1-ого в списке друга:
    // //*[@id="list_content"]//div[1]//div[contains(@class,'friends_user_row')][1]//div[contains(@class,'friends_field_title')]//a
    protected final String FRIEND_LIST_CONT_PART1 = "//*[@id=\"list_content\"]//div";                                    //+[currentBlock]
    protected final String FRIEND_LIST_ELEMENT_FULL_USER_INFO_PART2 = "//div[contains(@class,'friends_user_row')]";     //+[currentElementInBlock]
    //Ссылка и имя страницы конкретного пользователя (Обрезанная часть. Саму по себе использовать нельзя)
    protected final String FRIEND_LIST_ELEMENT_FIELD_TITLE_PART3 = "//div[contains(@class,'friends_field_title')]//a";

    //Общее количество друзей
    protected final String NUMBER_OF_FRIENDS_ON_PAGE_LIST_FRIENDS = "//*[@id=\"friends_tab_all\"]/a/span";
    protected final String NUMBER_OF_FRIENDS_ON_USER_PAGE = "//*[@id=\"wide_column\"]/div[1]/div[2]/a[2]/div[1]";

    //Просто полезный пирожок для наглядности
    protected String separator = "=============================================";

    //Количество друзе в одном отображаемом блоке
    //Надо бы получить программно, но пока пусть так
    protected int numberFriendsOnBox = 15;
    protected int numberPostsOnBlock = 10;

    //Безопасной количество лайков, при которых действия не считаются подозрительными
    protected int safetyNumberLikes = 30;

    SeleniumWorker() {
        /*//Штука, для подключения на компе без драйвера FireFox'a
        String driverDireсtory =System.getProperty("user.dir")+ "\\src\\drivers\\geckodriver.exe";
        System.setProperty("webdriver.gecko.driver",driverDireсtory);*/

        try {
            driver = new FirefoxDriver();
            //sleep(5);
            wait = new WebDriverWait(driver, 5);
        } catch (org.openqa.selenium.WebDriverException we) {
            System.out.println("Ошибка в конструкторе SeleniumWorker");
            we.getMessage();
            System.out.println(separator);
        }

    }

    //Стартовые методы
    public boolean authorization(String login, String password) {
        System.out.println(separator);
        System.out.println("Start authorization");
        driver.get(VK_URL);

        if (waitLoadOfElementByTypeOfElementXPath(ConstVK.WELCOME_PAGE)) {
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
            if (waitLoadOfElementByTypeOfElementXPath(ConstVK.HOST_USER_PAGE)) {
                System.out.println("Result authorization: true");
                sleep(2);

                //Заполним личные данные, что пригодятся потом
                hostPageLink = findHostPageLink();
                return true;
            } else {
                //Вручную введем капчу
                sleep(100);
                if (waitLoadOfElementByTypeOfElementXPath(ConstVK.HOST_USER_PAGE)) {
                    System.out.println("Result authorization: true");
                    sleep(2);

                    //Заполним личные данные, что пригодятся потом
                    hostPageLink = findHostPageLink();

                    return true;
                } else {
                    System.out.println("Ошибка входа. Неверный пароль/логин");
                    System.out.println("Result: false");
                    return false;
                }

            }
        } else {
            System.out.println("Ошибка загрузки стартовой страницы");
            System.out.println("Result: false");
            return false;
        }

    }
    public String findHostPageLink()throws LoadException{
        try{
            String btnMyPageXPath = "//*[@id=\"l_pr\"]/a/span/span[3]";
            driver.findElement(By.xpath(btnMyPageXPath)).click();
            try{
                waitLoadElementByTypeExp(ConstVK.USER_PAGE);
                return driver.getCurrentUrl();
            }catch (LoadException e){
                throw new LoadException("Не удалось загрузить рабочую страницу");
            }
        }catch (LoadException e){
            throw new LoadException("Не удалось найти кнопку 'Моя страница'");
        }


    }
    public String getHostPageLink() {
        return hostPageLink;
    }

    //Методы, связанные с работой со списками друзей
    public ArrayList<User> getHostFriendList() {
        System.out.println(separator);
        System.out.println("Start getHostFriendList");
        ArrayList<User> listFriends = new ArrayList<>();
        if (waitLoadOfElementByXPath(MY_FRIENDS_LINK)) {
            driver.findElement(By.xpath(MY_FRIENDS_LINK)).click();
            listFriends = sortOutFriendsListPage();
            //System.out.println(listFriends.size());
            System.out.println("Result getHostFriendList: true");
            System.out.println(separator);
        } else {
            System.out.println("Не удалось перейти на вкладку 'Друзья'");
            System.out.println(separator);
            //return false;
        }
        return listFriends;
    }

    public ArrayList<User> getUserFriendList(String pageLink) {
        System.out.println(separator);
        System.out.println("Start getUserFriendList");
        ArrayList<User> listFriends = new ArrayList<>();

        driver.get(pageLink);
        if (waitLoadOfElementByTypeOfElementXPath(ConstVK.USER_PAGE)) {
            if (waitLoadOfElementByXPath(USER_FRIENDS_LINK)) {
                driver.findElement(By.xpath(USER_FRIENDS_LINK)).click();
                //System.out.println("Открыли друзей пользователя");
                listFriends = sortOutFriendsListPage();
                //System.out.println(listFriends.size());
                System.out.println("Result getUserFriendList: true");
                System.out.println(separator);
            } else {
                System.out.println("Не удалось перейти на вкладку 'Друзья'");
                System.out.println(separator);
                //return false;
            }
        } else {
            System.out.println("Не удалось открыть страницу пользователя");
        }

        return listFriends;
    }

    private ArrayList<User> sortOutFriendsListPage() {
        ArrayList<User> listFriends = new ArrayList<>();
        if (waitLoadOfElementByXPath(NUMBER_OF_FRIENDS_ON_PAGE_LIST_FRIENDS)) {
            //Пройдет по открытой странице с друзьями и соберет краткую информацию о них в список
            int totalNumberOfFriends = Integer.parseInt(driver.findElement(By.xpath(NUMBER_OF_FRIENDS_ON_PAGE_LIST_FRIENDS)).getText());
            //System.out.println("totalNumber = " + totalNumberOfFriends);
            int countCurrentNumberOfFriends = 0;
            int countFriendsBlocks = 1;

            while (countCurrentNumberOfFriends < totalNumberOfFriends) {
                if (waitLoadOfElementByXPath(FRIEND_LIST_CONT_PART1 + "[" + countFriendsBlocks + "]")) {
                    //System.out.println("Вошли в " + countFriendsBlocks + "-ый блок друзей");
                    for (int i = 1; i <= numberFriendsOnBox && countCurrentNumberOfFriends < totalNumberOfFriends; i++) {
                        String currentFriendXPath = FRIEND_LIST_CONT_PART1 + "[" + countFriendsBlocks + "]" + FRIEND_LIST_ELEMENT_FULL_USER_INFO_PART2 + "[" + i + "]" + FRIEND_LIST_ELEMENT_FIELD_TITLE_PART3;
                        if (waitLoadOfElementByXPath(currentFriendXPath)) {
                            WebElement currentFriend = driver.findElement(By.xpath(currentFriendXPath));
                            String name = currentFriend.getText();
                            String pageLink = currentFriend.getAttribute("href");
                            User tempUser = new User(pageLink, name);
                            listFriends.add(tempUser);
                            countCurrentNumberOfFriends++;

                            /*System.out.println("#" + countCurrentNumberOfFriends);
                            tempUser.display();
                            System.out.println("-------------------");*/
                        } else {
                            System.out.println("Не удалось найти друга");
                            System.out.println(separator);
                            return listFriends;
                        }
                    }

                    scrollPageToBottom();
                    countFriendsBlocks++;
                } else {
                    System.out.println("Не удалось найти блок друзей");
                    System.out.println(separator);
                    return listFriends;
                }
            }

            return listFriends;
        } else {
            System.out.println("Не удалось найти общее количество друзей");
            System.out.println(separator);
            return listFriends;
        }
    }

    public User parseWebElementToUser(WebElement element) {
        //На вход получаем элемент с укороченной информацией о друге
        //Переходим по ссылке на самого пользователя и собираем нужную информацию

        //Вырезаем имя, чтобы по нему искать ссылку
        String name = element.getText().substring(0, element.getText().indexOf('\n'));
        //System.out.println("Выковыренное имя: '" + name + "'");

        String linkToCurrentFriendXPath = "//*[contains(@id,'friends_user_row')]" +
                "//div[contains(@class,'friends_user_info')]//div[contains(@class,'friends_field_title')]//" +
                "a[contains( text(),'" + name + "')]";
        WebElement linkToCurrentFriend = element.findElement(By.xpath(linkToCurrentFriendXPath));
        linkToCurrentFriend.click();

        //Подождем, пока страница пользователя прогрузится
        String pageNameXPath = "//*[@id=\"page_info_wrap\"]/div[1]/h2";
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(pageNameXPath)));

        //Активная ссылка на страницу пользователя
        //Из нее можно выковырять id
        String profileLink = driver.getCurrentUrl();
        System.out.println(profileLink);

        //Получаем имя страницы
        WebElement pageNameWebElement = driver.findElement(By.xpath(pageNameXPath));
        String pageName = pageNameWebElement.getText();

        return new User(profileLink, pageName);
    }


    //Все действия, связанные с лайками/репостами
    public boolean likeProfilePhoto(String pageLink) {
        System.out.println(separator);
        //Переходим на страницу пользователя
        driver.get(pageLink);

        if (waitLoadOfElementByTypeOfElementXPath(ConstVK.USER_PAGE)) {
            System.out.println("Страница пользователя доступна");
            //Нажимаем на фото пользователя
            //wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(PROFILE_PHOTO_XPATH)));
            WebElement profilePhoto = driver.findElement(By.xpath(PROFILE_PHOTO_XPATH));
            profilePhoto.click();

            //Ждем пока оно прогрузится
            if (waitLoadOfElementByTypeOfElementXPath(ConstVK.PHOTO_POST)) {
                System.out.println("Фотография профиля доступна");
                //Сначала проверим наличие собственного лайка
                if (wasCurrentPostLiked(ConstVK.PHOTO_POST)) {
                    System.out.println("Отметка 'Мне нравится' уже стоит");
                    System.out.println("Result: false");
                    System.out.println(separator);
                    return false;
                } else {
                    System.out.println("Отметка 'Мне нравится' не стоит. Поставим ее");
                    //Нажимаем кнопку 'Мне нравится'
                    likeCurrentPost(ConstVK.PHOTO_POST, ConstVK.LIKE);
                    /*WebElement btnLike = driver.findElement(By.xpath(btnLikeXPath));
                    btnLike.click();*/
                    System.out.println("Result: true");
                    System.out.println(separator);
                    return true;
                }

            } else {
                System.out.println("Фотография недоступна");
                System.out.println("Result: false");
                System.out.println(separator);
                return false;
            }
        } else {
            System.out.println("Страница пользователя недоступна");
            System.out.println("Result: false");
            System.out.println(separator);
            return false;
        }
    }

    public boolean likePostByLink(String linkPost, ConstVK typeOfPost, ConstVK typeOfAction) {
        driver.get(linkPost);
        return likeCurrentPost(typeOfPost, typeOfAction);
    }

    public boolean likeCurrentPost(ConstVK typeOfPost, ConstVK typeOfAction) {
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

    private boolean wasCurrentPostLiked(ConstVK typeOfPost) throws WebDriverException {
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


    public boolean likeSeveralPhotos(String pageLink, int numberPhotosForLike) {
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


    public boolean likeSeveralPostsOnPage(String pageLink, int totalNumberLikes) {
        //Пройдем по странице пользователя и будем лайкать посты
        //При необходимости скроллить страницу

        //Путь к блоку записи
        ////*[@id="page_wall_posts"]//div[contains(@class,'_post post page_block')]
        //*[@id="post226361909_2159"]/div/div[2]/div/div[2]/div

        //Путь к панели с кнопками 'Нравится' под каждым постом
        //*[@id="page_wall_posts"]//div[contains(@class,'_post post page_block')]/div/div[2]/div/div[2]/div

        //Путь конкретно к кнопке 'Мне нравится' под каждым постом
        //*[@id="page_wall_posts"]//div[contains(@class,'_post post page_block')]/div/div[2]/div/div[2]/div//div[contains(@class,'like_btns')]//a[1]

        System.out.println(separator);
        System.out.println("Start: likeSeveralPostsOnPage");

        driver.get(pageLink);

        if (waitLoadOfElementByTypeOfElementXPath(ConstVK.USER_PAGE)) {
            int countLike = 1;
            int realCountLike = 0;

            while (countLike <= totalNumberLikes) {
                //Поймаем момент, когда нужно пролистнуть страницу
                if (countLike % numberPostsOnBlock == 0) {
                    scrollPageToBottom();
                }
                String BTN_LIKE_POST_ON_WALL = "//*[@id=\"page_wall_posts\"]//div[contains(@class,'_post post page_block')]" + "[" + countLike + "]" +
                        "/div/div[2]/div/div[2]/div//div[contains(@class,'like_btns')]//a[1]";
                if (waitLoadOfElementByXPath(BTN_LIKE_POST_ON_WALL)) {
                    //Проверим, стоит ли лайк
                    String BTN_LIKE_POST_ON_WALL_ACTIVE = "//*[@id=\"page_wall_posts\"]//div[contains(@class,'_post post page_block')]" + "[" + countLike + "]" +
                            "/div/div[2]/div/div[2]/div//div[contains(@class,'like_btns')]//a[1][contains(@class,'active')]";
                    if (!waitLoadOfElementByXPath(BTN_LIKE_POST_ON_WALL_ACTIVE)) {
                        //Т.е. пост не лайкали
                        if (shouldPostBeLiked()) {
                            driver.findElement(By.xpath(BTN_LIKE_POST_ON_WALL)).click();
                            System.out.println("Лайкнули " + countLike + "-ый пост");
                            realCountLike++;
                        } else {
                            System.out.println("Пропустили " + countLike + "-ый пост, ибо так решила судьба");
                        }

                    } else {
                        System.out.println("Не лайкали, но просмотрели " + countLike + "-ый пост");
                    }
                    countLike++;
                } else {
                    System.out.println("Не удалось найти запись");
                    System.out.println(separator);
                    return false;
                }
            }
            System.out.println("Итого у пользователя '" + pageLink + "' пролайкали " + realCountLike + " записей");

            return true;

        } else {
            System.out.println("Не удалось загрузить страницу пользователя");
            System.out.println(separator);
            return false;
        }

    }


    //Вспомогательные методы
    protected void sleep(int seconds) {
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
                elementXPath = BTNS_LIKE_SHARE_PANEL_WL_POST_XPATH;
                break;
            case PHOTO_POST:
                elementXPath = BTNS_LIKE_SHARE_PANEL_PHOTO_XPATH;
                break;
            case USER_PAGE:
                elementXPath = PROFILE_NAME_XPATH;
                break;
            case WELCOME_PAGE:
                elementXPath = LOGIN_PANEL_XPATH;
                break;
            case HOST_USER_PAGE:
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
        String elementXPath = new String();
        switch (typeOfElement) {
            case WL_POST:
                elementXPath = BTNS_LIKE_SHARE_PANEL_WL_POST_XPATH;
                break;
            case PHOTO_POST:
                elementXPath = BTNS_LIKE_SHARE_PANEL_PHOTO_XPATH;
                break;
            case USER_PAGE:
                elementXPath = PROFILE_NAME_XPATH;
                break;
            case WELCOME_PAGE:
                elementXPath = LOGIN_PANEL_XPATH;
                break;
            case HOST_USER_PAGE:
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
            return getStartInfoUserOnPage(profileLink);
        } catch (LoadException e) {
            throw new LoadException("Не удалось загрузить страницу пользователя");
        }
    }
    public User getStartInfoUserOnPage(String profileLink) throws LoadException,SearchIDException{
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

    public User getFullInfoFromUserOnPage(String pageLink) throws LoadException{
        try{
            User temp = getStartInfoUserByPage(pageLink);
            try{
                temp.setHostProfileLink(getHostPageLink());
            }catch (LoadException e){
                //throw new LoadException("getFullInfoFromUserOnPage: не удалось получить hostPageLink");
                e.printStackTrace();
            }
            try{
                temp.setDateBirth(getUserDateBirthOnPage());
                temp.setAge(temp.calculateAge());
            }catch (LoadException e){
                //throw new LoadException("getFullInfoFromUserOnPage: не удалось получить dateBirthday");
                e.printStackTrace();
            }
            try{
                temp.setCity(getUserCityOnPage());
            }catch (LoadException e){
                //throw new LoadException("getFullInfoFromUserOnPage: не удалось получить city");
                e.printStackTrace();
            }
            try{
                temp.setStatusRequestAnswer(getFriendStatusOnPage());
            }catch (LoadException e){
                //throw new LoadException("getFullInfoFromUserOnPage: не удалось получить StatusRequestAnswer");
                e.printStackTrace();
            }
            try{
                temp.setCountFriends(getCountInfoUserOnPage(ConstVK.COUNT_ALL_FRIENDS));
            }catch (LoadException e){
                //throw new LoadException("getFullInfoFromUserOnPage: не удалось получить CountAllFriends");
                e.printStackTrace();
            }
            try{
                temp.setCountCommonFriends(getCountInfoUserOnPage(ConstVK.COUNT_COMMON_FRIENDS));
            }catch (LoadException e){
                //throw new LoadException("getFullInfoFromUserOnPage: не удалось получить CountCommonFriends");
                e.printStackTrace();
            }
            try{
                temp.setCountFollowers(getCountInfoUserOnPage(ConstVK.COUNT_FOLLOWERS));
            }catch (LoadException e){
                //throw new LoadException("getFullInfoFromUserOnPage: не удалось получить CountFollowers");
                e.printStackTrace();
            }

            SimpleDateFormat formatForDateNow = new SimpleDateFormat("YYYY-MM-dd");
            String currentDate = formatForDateNow.format(new Date());
            temp.setDateRequest(currentDate);

            return temp;
        }catch (LoadException e){
            e.printStackTrace();
            throw new LoadException("getFullInfoFromUserOnPage:LoadException: не удалось получить необходитую минимальную информацию о пользователе");
        } catch (SearchIDException e) {
            throw new LoadException("getFullInfoFromUserOnPage:SearchIDException: не удалось получить необходитую минимальную информацию о пользователе");
        }
    }
    public User getFullInfoFromUserByPage(String pageLink) throws LoadException{
        try{
            openUserPage(pageLink);
            return getFullInfoFromUserOnPage(pageLink);
        }catch (LoadException e){
            throw new LoadException("getFullInfoFromUserByPage: не удалось загрузить страницу пользователя");
        }
    }

    //Получаем имя пользователя
    public String getUserNameOnPage()throws LoadException{
        try{
            return driver.findElement(By.xpath(PROFILE_NAME_XPATH)).getText();
        }catch (LoadException e){
            throw new LoadException("Не удалось получить имя пользователя");
        }
    }
    public String getUserNameByPage(String pageLink)throws LoadException{
        try {
            openUserPage(pageLink);
            return getUserNameOnPage();
        } catch (LoadException e) {
            throw new LoadException("Не удалось загрузить страницу пользователя");
        }
    }

    //Получаем исходный ID пользователя
    public int getDefaultIDUserByPage(String pageLink) throws LoadException, SearchIDException {
        try {
            openUserPage(pageLink);
            return getDefaultIDUserOnPage();
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
    public String getUserCityByLink(String pageLink) throws LoadException {
        driver.get(pageLink);
        try {
            waitLoadElementByTypeExp(ConstVK.USER_PAGE);
            return getUserCityOnPage();
        } catch (LoadException e) {
            throw new LoadException("getUserCityByLink: не удалось загрузить страницу пользователя");
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

    //Получаем дату пользователя
    public String getUserDateBirthByPage(String pageLink) throws LoadException {
        driver.get(pageLink);
        try {
            waitLoadElementByTypeExp(ConstVK.USER_PAGE);
            return getUserDateBirthOnPage();
        } catch (LoadException e) {
            throw new LoadException("getUserDateBirthByPage: не удалось загрузить страницу пользователя");
        }
    }
    public String getUserDateBirthOnPage()throws LoadException{
        //Получим дату рождения
        String dayAndMonthLinkXPath = "//*[@id=\"profile_short\"]/div[1]/div[2]/a[1]";
        String yearLinkXPath = "//*[@id=\"profile_short\"]/div[1]/div[2]/a[2]";

        String dateOfBirth = null;

        try{
            waitLoadElementExp(yearLinkXPath);
            String byearLink = driver.findElement(By.xpath(yearLinkXPath)).getAttribute("href");
            int byear = hp.getBDigit(byearLink, ConstVK.BYEAR);
            dateOfBirth = String.valueOf(byear);
        }catch (LoadException e){
            dateOfBirth = "0000";
        }
        try{
            waitLoadElementExp(dayAndMonthLinkXPath);
            String bdayAndMonthLink = driver.findElement(By.xpath(dayAndMonthLinkXPath)).getAttribute("href");
            dateOfBirth += hp.getDayAndMonthStr(bdayAndMonthLink);

            /*int bday = hp.getBDigit(bdayAndMonthLink, ConstVK.BDAY);
            int bmonth = hp.getBDigit(bdayAndMonthLink, ConstVK.BMONTH);*/
        }catch (LoadException e){
            dateOfBirth += "-00-00";
            //throw new LoadException("Не удалось загрузить ссылку для дня и месяца рождения");
        }
        return dateOfBirth;

    }

    //Получаем статус заявки в друзья пользователя
    public int getFriendStatusByPage(String pageLink) throws LoadException {
        driver.get(pageLink);
        try {
            waitLoadElementByTypeExp(ConstVK.USER_PAGE);
            return getFriendStatusOnPage();
        } catch (LoadException e) {
            throw new LoadException("getFriendStatusByPage: не удалось загрузить страницу");
        }

    }
    public int getFriendStatusOnPage() {
        try {
            String btnActionsWithFriend = "//*[@id=\"friend_status\"]/div[contains(@class,'flat_button button_wide secondary page_actions_btn')]/span";
            waitLoadElementExp(btnActionsWithFriend);
            String msg = driver.findElement(By.xpath(btnActionsWithFriend)).getText();
            if (msg.equalsIgnoreCase("У Вас в друзьях") | msg.equalsIgnoreCase("In your friend list")) {
                return 1;
            } else {
                if (msg.equalsIgnoreCase("Заявка отправлена") | msg.equalsIgnoreCase("Request sent")) {
                    return 0;
                } else {
                    return -1;
                }
            }
        } catch (LoadException e) {
            throw new LoadException("Не удалось проверить статус");
        }
    }
    public boolean isMyFriendByPage(String pageLink) throws LoadException {
        driver.get(pageLink);
        try {
            waitLoadElementByTypeExp(ConstVK.USER_PAGE);
            return isMyFriendOnPage();

        } catch (LoadException e) {
            throw new LoadException("Не удалось загрузить страницу пользователя");
        }
    }
    private boolean isMyFriendOnPage() {
        try {
            String btnActionsWithFriend = "//*[@id=\"friend_status\"]/div[contains(@class,'flat_button button_wide secondary page_actions_btn')]/span";
            waitLoadElementExp(btnActionsWithFriend);
            String msg = driver.findElement(By.xpath(btnActionsWithFriend)).getText();
            return (msg.equalsIgnoreCase("У Вас в друзьях") | msg.equalsIgnoreCase("In your friend list"));
        } catch (LoadException e) {
            throw new LoadException("Не удалось проверить статус");
        }

    }

    //Получаем информацию и количестве друзей/подписчиков
    public int getCountInfoUserByPage(String pageLink, ConstVK typeCount) throws LoadException {
        driver.get(pageLink);
        try {
            waitLoadElementByTypeExp(ConstVK.USER_PAGE);
            return getCountInfoUserOnPage(typeCount);
        } catch (LoadException e) {
            throw new LoadException("getCountInfoUserByPage: не удалось загрузить страницу");
        }

    }
    private int getCountInfoUserOnPage(ConstVK typeCount) {
        //Вызывается со страницы пользрвателя
        int result = 0;
        //Панель со всеми счетчиками страницы: кол-во друзей, подписчиков, общих друзей
        String COUNTS_MODULE = "//*[@id=\"wide_column\"]/div[1]/div[2]";
        String COUNT_COMMON_FRIENDS = "//*[@id=\"wide_column\"]/div[1]/div[2]//a[contains(@href,'common')]//div[contains(@class,'count')]";
        String COUNT_ALL_FRIENDS = "//*[@id=\"wide_column\"]/div[1]/div[2]//a[contains(@href,'all')]//div[contains(@class,'count')]";
        String COUNT_FOLLOWERS = "//*[@id=\"wide_column\"]/div[1]/div[2]//a[contains(@href,'#')]//div[contains(@class,'count')]";

        if (waitLoadOfElementByXPath(COUNTS_MODULE)) {
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
            if (waitLoadOfElementByXPath(currentCountXPath)) {
                //Если такой счетчик существует
                String forResult = driver.findElement(By.xpath(currentCountXPath)).getText();
                //System.out.println(typeCount + " = "+forResult);
                try {
                    result = Integer.parseInt(forResult);
                } catch (NumberFormatException e) {
                    System.out.println("Ошибка преобразования  '" + forResult + "' в int");
                }

            } else {
                System.out.println("Информации о " + typeCount + " на странице не найдено");
            }
        } else {
            System.out.println("Не удалось загрузить COUNTS_MODULE на странице пользователя");
        }

        return result;

    }




    //Добавить в друзья
    public void addUserToFriendList(String pageLink) throws AddToFriendlistException {
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
                throw new AddToFriendlistException("Не удалось добавить в друзья");
            }
        } catch (LoadException e) {
            //Не удалось загрузить страницу пользователя
            System.out.println(e.getMessage());
            throw new AddToFriendlistException("Не удалось добавить в друзья");
        }
    }

    //Написать сообщение
    public void writeMessageByLink(String pageLink, String msg) throws LoadException {
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

                driver.findElement(By.xpath(textArea)).sendKeys(msg);

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
                //Сравним с запрашиваемым числом

                //Добавим подписчиков в список
                String memberLinkXPath = "//*[@id=\"fans_rowsmembers\"]/div[contains(@class,'fans_fan_row')]/div[contains(@class,'fans_fan_name')]/a";
                waitLoadElementExp(memberLinkXPath);

                ArrayList<String> listMembersLink = new ArrayList<>(numberMembersTotal);

                int k = 0;
                while (k < numberMembersTotal) {
                    List<WebElement> listMembersWebEl = driver.findElements(By.xpath(memberLinkXPath));
                    int numberMembersOnBlock = 60;      //Первоначально отображается 60 подписчиков, затем добавляется по 30
                    if (k == numberMembersOnBlock) {
                        numberMembersOnBlock = 30;
                    }
                    for (int i = 0; i < numberMembersOnBlock && k < numberMembersTotal; i++) {
                        //System.out.println("#" + k + " " + listMembersWebEl.get(k).getAttribute("href"));//driver.findElement(By.xpath(memberLinkXPath)).getText());
                        listMembersLink.add(listMembersWebEl.get(k).getAttribute("href"));
                        k++;
                    }
                    String btnScroll = "//*[@id=\"fans_more_linkmembers\"]";
                    driver.findElement(By.xpath(btnScroll)).click();

                }

                return listMembersLink;

            } catch (LoadException e) {
                throw new LoadException("Не удалось загрузить участников группы");
            }
        } catch (LoadException e) {
            throw new LoadException("Не удалось загрузить страницу группы");
        }

    }

}

