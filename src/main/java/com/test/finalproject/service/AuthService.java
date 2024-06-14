package com.test.finalproject.service;

import com.test.finalproject.model.dtos.auth.*;

public interface AuthService {

    AuthRes login(AuthReq req);

    AuthRes register(RegisterReq req);

    void changePassword(ChangePasswordReq req, String token);

    void createVerification(String email);

    void confirmVerification(String token,String email);

    void forgotPassword(ForgotPasswordReq req);
}
