package com.test.finalproject.exception;

public class NotFoundException extends CustomException {

    public NotFoundException(String code, String message) {
        super(code, message);
    }

    public NotFoundException(String message) {
        super("404", message);
    }


}
