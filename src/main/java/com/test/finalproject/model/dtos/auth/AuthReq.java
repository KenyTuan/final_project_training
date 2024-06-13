package com.test.finalproject.model.dtos.auth;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter @Builder
public class AuthReq {

    @Size(min = 5,max = 255,message = "Username Invalid!")
    private String username;

    @Size(min = 8, message = "Password Invalid!")
    private String password;

}