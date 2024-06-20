package com.test.finalproject.service;

import com.test.finalproject.model.dtos.auth.*;

public interface AuthService {

    AuthRes login(AuthReq req);

    AuthRes register(RegisterReq req);

    void requestForgotPassword(String email);

    void forgotPassword(RequestForgot requestForgot);
}
