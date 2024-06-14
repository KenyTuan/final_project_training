package com.test.finalproject.model.dtos.auth;

import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class ChangePasswordReq {

    @Pattern(
            message = "Password Invalid",
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\\\d)(?=.*[@$!%*?&])[A-Za-z\\\\d@$!%*?&]{8,}$"
    )
    private String newPassword;

    @Pattern(
            message = "Password Invalid",
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\\\d)(?=.*[@$!%*?&])[A-Za-z\\\\d@$!%*?&]{8,}$"
    )
    private String oldPassword;


}
