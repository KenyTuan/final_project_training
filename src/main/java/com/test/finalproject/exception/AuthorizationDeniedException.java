package com.test.finalproject.exception;

public class AuthorizationDeniedException extends CustomException {

    public AuthorizationDeniedException(String code, String message) {
        super(code, message);
    }

    public AuthorizationDeniedException(String message) {
        super("400",message);
    }
}
