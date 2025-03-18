package com.spinteg.lv.ExceptionHandling;

public class CustomDataMissingException extends RuntimeException {
    public CustomDataMissingException(String message) {
        super(message);
    }
}
