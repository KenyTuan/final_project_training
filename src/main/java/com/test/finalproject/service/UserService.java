package com.test.finalproject.service;


import com.test.finalproject.model.dtos.user.UserRes;

import java.util.List;

public interface UserService {

    List<UserRes> getAllUsers();

    UserRes updateUserLock(int id);
}
