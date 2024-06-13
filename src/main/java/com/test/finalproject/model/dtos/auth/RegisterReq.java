package com.test.finalproject.model.dtos.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class RegisterReq {

    @Size(min = 5,max = 255,message = "Username Invalid!")
    private String username;

    @Size(min = 8, message = "Password Invalid!")
    private String password;

    @Email(message = "Email Invalid!")
    private String email;

    @NotBlank(message = "First Name is required")
    private String firstName;

    @NotBlank(message = "Last Name is required")
    private String lastName;
}
