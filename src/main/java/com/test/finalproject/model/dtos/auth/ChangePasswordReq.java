package com.test.finalproject.model.dtos.auth;

import com.test.finalproject.constants.MessageException;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class ChangePasswordReq {

    @Pattern(
            message = MessageException.INVALID_PASSWORD_NEW,
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$"
    )
    private String newPassword;

    @Pattern(
            message = MessageException.INVALID_PASSWORD_OLD,
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$"
    )
    private String oldPassword;


}
