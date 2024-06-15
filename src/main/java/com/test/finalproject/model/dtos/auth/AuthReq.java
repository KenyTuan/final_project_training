package com.test.finalproject.model.dtos.auth;

import com.test.finalproject.constants.MessageException;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter @Builder
public class AuthReq {

    @Size(min = 5,max = 255,message = MessageException.INVALID_USERNAME)
    private String username;

    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$",
            message = MessageException.INVALID_PASSWORD
    )
    private String password;

}
