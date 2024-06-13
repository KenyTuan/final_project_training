package com.test.finalproject.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class CustomException extends RuntimeException {

    public String code;

    public String message;
}
