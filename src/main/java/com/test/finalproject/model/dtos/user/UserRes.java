package com.test.finalproject.model.dtos.user;

import com.test.finalproject.enums.AccountStatus;
import com.test.finalproject.enums.RoleName;
import com.test.finalproject.model.dtos.task.TaskRes;

import java.util.List;

public record UserRes(
        Integer id,
        String username,
        String firstName,
        String lastName,
        String email,
        RoleName role,
        AccountStatus status,
        List<TaskRes> tasks
) {
}
