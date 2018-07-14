package ru.nastinio;

public enum ConstVK {

    //Тип поста: пост на стене или фотография
    WL_POST,
    PHOTO_POST,

    //Действия с записью
    LIKE,
    DISLIKE,

    //Тип загружаемого элемента: страница пользователя, пост и т.д.
    USER_PAGE,          //Проверяет отображение имени страницы
    HOST_USER_PAGE,     //Ссылка на страницу хозяйина
    WELCOME_PAGE,       //Стартовая страница для авторизации

    //XPATH,              //Поиск сразу по xpath

    //Навигация для записей
    LEFT,
    RIGHT,

    //Для того, чтобы методы по получению даты рождения выглядели посимпатичнее
    BDAY,
    BMONTH,
    BYEAR,

    //Для сбора количественной информации о странице
    COUNT_ALL_FRIENDS,
    COUNT_COMMON_FRIENDS,
    COUNT_FOLLOWERS

}
