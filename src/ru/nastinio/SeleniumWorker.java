package ru.nastinio;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;

public class SeleniumWorker {

    private static WebDriver driver = new FirefoxDriver();
    private WebDriverWait wait = new WebDriverWait(driver, 20);

    private final String VK_URL = "https://vk.com/";

    //Элементы для авторизации
    private final String LOGIN_PANEL_XPATH = "//*[@id=\"index_login_form\"]";   //Вся панель авторизации
    private final String INPUT_LOGIN_XPATH = ".//*[@id='index_email']";         //Поле ввода логина
    private final String INPUT_PASSWORD_XPATH = ".//*[@id='index_pass']";       //Поле ввода пароля
    private final String BTN_LOG_XPATH = ".//*[@id='index_login_button']";      //Кнопка войти
    private final String TOP_PROFILE_LINK = "//*[@id=\"top_profile_link\"]";    //Ссылка на страницу хозяина. Для проверки входа

    //Панель кнопок 'Нравится' и 'Поделиться' для поста на стене
    private final String BTNS_LIKE_SHARE_PANEL_WL_POST_XPATH = "//*[@id=\"wl_post_actions_wrap\"]";
    private final String BTNS_LIKE_SHARE_PANEL_PHOTO_XPATH = "//*[@id=\"pv_narrow\"]/div[1]/div[1]/div/div/div[1]/div[3]/div/div[1]";

    //Кнопка 'Мне нравится'
    private final String BTN_LIKE_WL_POST_XPATH = "//*[@id=\"wl_post_actions_wrap\"]/div/div/div[1]/a[1]";
    private final String BTN_LIKE_PHOTO_XPATH = "//*[@id=\"pv_narrow\"]/div[1]/div[1]/div/div/div[1]/div[3]/div/div[1]/a[1]";

    //Активная кнопка 'Мне нравится'
    private final String BTN_LIKE_ACTIVE_WL_POST_XPATH = "//*[@id=\"wl_post_actions_wrap\"]/div/div/div[1]/" +
            "a[contains(@class,'like active')][1]";
    private final String BTN_LIKE_ACTIVE_PHOTO_XPATH = "//*[@id=\"pv_narrow\"]/div[1]/div[1]/div/div/div[1]/div[3]/div/div[1]//" +
            "a[contains(@class,'like active')]";

    //Вспомогательные элементы страницы
    private final String PROFILE_NAME_XPATH = "//*[@id=\"page_info_wrap\"]/div[1]/h2";
    private final String PROFILE_PHOTO_XPATH = "//*[@id=\"profile_photo_link\"]/img";
    private final String PHOTO_GALLERY_XPATH = "//*[@id=\"page_photos_module\"]/a[1]";

    //Область фотографии, чтобы появились кнопки навигации влево и вправо
    private final String PHOTO_AREA_FOR_SHOW_NAV_BTN_XPATH = "//*[@id=\"pv_photo\"]";
    //Кнопки 'влево' и 'вправо' в галерее фотографий
    private final String BTN_NAV_PHOTO_LEFT_XPATH = "//*[@id=\"pv_nav_btn_left\"]/div";
    private final String BTN_NAV_PHOTO_RIGHT_XPATH = "//*[@id=\"pv_nav_btn_right\"]/div";

    //Кнопки 'влево' и 'вправо' для записей на стене
    private final String BTN_NAV_WL_POST_RIGHT_XPATH = "";
    private final String BTN_NAV_WL_POST_LEFT_XPATH = "";

    //Просто полезный пирожок для наглядности
    private String separator = "=============================================";


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
                return true;
            } else {
                System.out.println("Ошибка входа. Неверный пароль/логин");
                System.out.println("Result: false");
                return false;
            }
        } else {
            System.out.println("Ошибка загрузки стартовой страницы");
            System.out.println("Result: false");
            return false;
        }

    }

    public boolean clickByXPath(String xpath) {
        System.out.println(separator);
        if (waitLoadOfElementByXPath(xpath)) {
            try {
                WebElement tempElement = driver.findElement(By.xpath(xpath));
                tempElement.click();
                System.out.println("Result clickByXPath: true");
                System.out.println(separator);
                return true;
            } catch (WebDriverException e) {
                System.out.println("Ошибка в clickByXPath");
                System.out.println(e.getMessage());
                System.out.println(separator);
                return false;
            }
        } else {
            System.out.println("Элемент не найден");
            System.out.println(separator);
            return false;
        }

    }

    public void getFriendsList() {
        //Берет список первых 15 друзей
        //Нужно прикрутить прокрутку страницы
        try {
            String friendsClassName = "friends_user_row";

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className(friendsClassName)));

            List<WebElement> listFriendsWebEl = driver.findElements(By.className(friendsClassName));
            List<User> listFriends = new ArrayList<>(listFriendsWebEl.size());

            for (WebElement current : listFriendsWebEl) {
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.className(friendsClassName)));

                User tempUser = parseWebElementToUser(current);
                System.out.println("================");
                tempUser.display();
                System.out.println("================");
                listFriends.add(tempUser);

                driver.navigate().back();
            }
        } catch (WebDriverException e) {
            System.out.println("Что-то пошло не так в getFriendsList: " + e.getMessage());
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

    public boolean likesAllPhotos(String pageLink) {
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

                int countLikes = 3;
                for (int i = 0; i < countLikes; i++) {
                    sleep(10);
                    //Нажмем на следующее фото и зациклим
                    if(getNextLeftPhoto()){
                        waitLoadOfElementByTypeOfElementXPath(ConstVK.PHOTO_POST);
                        System.out.println("Дождались загрузки следующей фотографии");
                        //Лайкнем его или уберем лайк для наглядности работы
                        if (wasCurrentPostLiked(ConstVK.PHOTO_POST)) {
                            likeCurrentPost(ConstVK.PHOTO_POST, ConstVK.DISLIKE);
                        } else {
                            likeCurrentPost(ConstVK.PHOTO_POST, ConstVK.LIKE);
                        }
                    }else{
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

    private boolean getNextLeftPhoto(){
        if(waitLoadOfElementByXPath(PHOTO_AREA_FOR_SHOW_NAV_BTN_XPATH)){
            WebElement element = driver.findElement(By.xpath(PHOTO_AREA_FOR_SHOW_NAV_BTN_XPATH));
            Actions actions = new Actions(driver);
            actions.moveToElement(element).perform();
            sleep(2);

            //String btnLeftIconXPath = "//*[@id=\"pv_nav_btn_left\"]/div";
            if(waitLoadOfElementByXPath(BTN_NAV_PHOTO_LEFT_XPATH)){
                WebElement btnLeft = driver.findElement(By.xpath(BTN_NAV_PHOTO_LEFT_XPATH));
                btnLeft.click();
                System.out.println("Нажали кнопку навигации");
                System.out.println(separator);
                return true;
            }else{
                System.out.println("Не удалось отобразить кнопку навигации");
                System.out.println(separator);
                return false;
            }
            //wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(BTN_NAV_PHOTO_LEFT_XPATH)));
        }else{
            System.out.println("Не удалось найти PHOTO_AREA_FOR_SHOW_NAV_BTN_XPATH");
            System.out.println(separator);
            return false;
        }

    }

    private void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            System.out.println("----------------------");
            System.out.println("Ошибка в методе sleep");
            System.out.println(e.getMessage());
            System.out.println("----------------------");
        }
    }

    private boolean waitLoadOfElementByXPath(String xpath) {
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

    private boolean waitLoadOfElementByTypeOfElementXPath(ConstVK typeOfElement) {
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


}

