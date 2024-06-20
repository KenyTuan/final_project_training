package com.test.finalproject.model.dtos.auth;

import com.test.finalproject.constants.MessageException;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter @Builder
public class RequestForgot implements Serializable {

    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$",
            message = MessageException.INVALID_PASSWORD
    )
    private String password;

    @Email(message = MessageException.INVALID_EMAIL)
    private String email;

    @NotBlank(message = MessageException.REQUIRED_TOKEN)
    private String token;
}