package com.test.finalproject.service;

import com.test.finalproject.model.dtos.auth.*;

public interface AuthService {

    AuthRes login(AuthReq req);

    AuthRes register(RegisterReq req);

    void createPasswordRestToken(String email);

    void confirmPasswordRestToken(String token, String email);
}
