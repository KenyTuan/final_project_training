package com.test.finalproject.exception;

public class BadRequestException extends CustomException{
    public BadRequestException(String code, String message) {
        super(code, message);
    }

    public BadRequestException(String message) {
        super("400",message);
    }
}
