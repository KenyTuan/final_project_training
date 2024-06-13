package com.test.finalproject.service;


import com.test.finalproject.model.dtos.user.UserRes;

import java.util.List;

public interface UserService {

    List<UserRes> getAll();

    void updateUserLock(int id);
}
