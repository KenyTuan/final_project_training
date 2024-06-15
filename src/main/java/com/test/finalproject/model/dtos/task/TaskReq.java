package com.test.finalproject.model.dtos.task;

import com.test.finalproject.constants.MessageException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class TaskReq {

    @NotBlank(message = MessageException.REQUIRED_NAME)
    private String name;

    @NotNull(message = MessageException.REQUIRED_USER_ID)
    private Integer userId;

}
