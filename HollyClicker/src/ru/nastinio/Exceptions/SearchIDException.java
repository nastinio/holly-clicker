package ru.nastinio.Exceptions;

import org.openqa.selenium.WebDriverException;

public class SearchIDException extends Exception {
    public SearchIDException(String msg) {
        super(msg);
    }
}