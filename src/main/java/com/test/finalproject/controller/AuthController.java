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
    public AuthRes login(@RequestBody @Valid AuthReq authReq) {
        return authService.login(authReq);
    }

    @PostMapping(ApiEndpoints.ACC_V1 + "/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthRes register(@RequestBody @Valid RegisterReq authReq) {
        return authService.register(authReq);
    }

    @PostMapping(ApiEndpoints.ACC_V1 + "/generate-password-rest-token")
    @ResponseStatus(HttpStatus.CREATED)
    public void createPasswordRestToken(@RequestParam(name = "email") String email) {
        authService.createPasswordRestToken(email);
    }

    @PatchMapping(ApiEndpoints.ACC_V1 +"/confirm-password-rest-token")
    public void confirmPasswordRestToken(
            @RequestParam(name = "email") String email,
            @RequestParam(name = "token") String token) {
        authService.confirmPasswordRestToken(token,email);
    }

}
