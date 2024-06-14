package com.test.finalproject.model.dtos.auth;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class ChangePasswordReq {

    @Size(min = 8, message = "New Password Invalid")
    private String newPassword;

    @Size(min = 8, message = "Old Password Invalid")
    private String oldPassword;


}
