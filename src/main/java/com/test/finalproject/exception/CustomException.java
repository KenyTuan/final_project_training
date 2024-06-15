package com.test.finalproject.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class CustomException extends RuntimeException {

    public String code;

    public String message;
}
