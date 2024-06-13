package com.test.finalproject.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiEndpoints {

    public static final String PREFIX = "/api";
    public static final String USER_V1 = "/v1/users";
    public static final String ACC_V1 = "/v1/account";
    public static final String TASK_V1 = "/v1/tasks";
    public static final String TASK_DETAIL_V1 = "/v1/taskDetails";

}
