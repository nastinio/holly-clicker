package ru.nastinio.Exceptions;

import org.openqa.selenium.WebDriverException;

public class AddToFriendlistException extends WebDriverException {
    public AddToFriendlistException(String msg){
        super(msg);
    }
}