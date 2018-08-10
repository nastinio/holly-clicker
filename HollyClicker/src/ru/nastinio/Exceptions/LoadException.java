package ru.nastinio.Exceptions;

import org.openqa.selenium.WebDriverException;

public class LoadException extends WebDriverException {
    public LoadException(String msg){
        super(msg);
    }
}

