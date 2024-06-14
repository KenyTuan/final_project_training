package com.test.finalproject.controller;

import com.test.finalproject.constants.ApiEndpoints;
import com.test.finalproject.model.dtos.auth.*;
import com.test.finalproject.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiEndpoints.PREFIX)
public class AuthController {

    private final AuthService authService;


    @PostMapping(ApiEndpoints.ACC_V1 + "/login")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthRes login(@RequestBody @Valid AuthReq authReq) {
        return authService.login(authReq);
    }

    @PostMapping(ApiEndpoints.ACC_V1 + "/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthRes register(@RequestBody @Valid RegisterReq authReq) {
        return authService.register(authReq);
    }

    @PatchMapping(ApiEndpoints.ACC_V1 + "/changePassword")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void forgot(@RequestBody @Valid ChangePasswordReq req,
                       @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        authService.changePassword(req,token);
    }

    @PostMapping(ApiEndpoints.ACC_V1 + "/sendVerifyEmail")
    @ResponseStatus(HttpStatus.CREATED)
    public void createTokenVerify(@RequestParam(name = "email") String email) {
        authService.createVerification(email);
    }

    @PostMapping(ApiEndpoints.ACC_V1 +"/verifyEmail")
    public void verifyEmail(
            @RequestParam(name = "email") String email,
            @RequestParam(name = "token") String token) {
        authService.confirmVerification(token,email);
    }

    @PatchMapping(ApiEndpoints.ACC_V1 + "/forgotPassword")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void forgot(@RequestBody @Valid ForgotPasswordReq req) {
        authService.forgotPassword(req);
    }

}
