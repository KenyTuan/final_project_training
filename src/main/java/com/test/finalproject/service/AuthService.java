package com.test.finalproject.service;

import com.test.finalproject.model.dtos.auth.AuthReq;
import com.test.finalproject.model.dtos.auth.AuthRes;
import com.test.finalproject.model.dtos.auth.ForgotPasswordReq;
import com.test.finalproject.model.dtos.auth.RegisterReq;

public interface AuthService {

    AuthRes login(AuthReq req);

    AuthRes register(RegisterReq req);

    void changePassword(ForgotPasswordReq req,String token);

    void createVerification(String tokenUser);

    void confirmVerification(String token,String tokenUser);

}
