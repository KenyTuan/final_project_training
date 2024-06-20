package com.test.finalproject.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageException {
    public static final String NOT_FOUND_USER = "User Not Found!";
    public static final String NOT_FOUND_TASK = "Task Not Found!";
    public static final String NOT_FOUND_TASK_DETAIL = "Task Detail Not Found!";
    public static final String NOT_FOUND_TOKEN_VERIFY = "Token Verify Not Found!";
    public static final String NOT_MATCH_PASSWORD = "Not Match Password!";
    public static final String ACCOUNT_LOCKED = "Account Is Locked!";
    public static final String ALREADY_EXIST_USERNAME_OR_EMAIL = "Username Or Email Already Exist!";
    public static final String TOKEN_EXPIRED= "Token Expired!";
    public static final String TASK_IS_COMPLETED = "Task Is Completed!";
    public static final String INVALID_USERNAME = "Username Invalid!";
    public static final String INVALID_EMAIL = "Email Invalid!";
    public static final String INVALID_PASSWORD = "Password Invalid!";
    public static final String INVALID_PASSWORD_OLD = "Password Old Invalid!";
    public static final String INVALID_PASSWORD_NEW = "Password New Invalid!";
    public static final String REQUIRED_FIRST_NAME = "First Name Is Required!";
    public static final String REQUIRED_LAST_NAME = "Last Name Is Required!";
    public static final String REQUIRED_NAME = "Name Is Required!";
    public static final String REQUIRED_USER_ID = "User ID Is Required!";
    public static final String REQUIRED_TASK_ID = "Task ID Is Required!";
    public static final String REQUIRED_TOKEN = "Token Is Required!";
}
