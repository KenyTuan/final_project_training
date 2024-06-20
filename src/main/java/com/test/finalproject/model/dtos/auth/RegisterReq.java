package com.test.finalproject.model.dtos.auth;

import com.test.finalproject.constants.MessageException;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter @Builder
public class RegisterReq implements Serializable {

    @Size(min = 5,max = 255,message = MessageException.INVALID_USERNAME)
    private String username;

    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$",
            message = MessageException.INVALID_PASSWORD
    )
    private String password;

    @Email(message = MessageException.INVALID_EMAIL)
    private String email;

    @NotBlank(message = MessageException.REQUIRED_FIRST_NAME)
    private String firstName;

    @NotBlank(message = MessageException.REQUIRED_LAST_NAME)
    private String lastName;
}
