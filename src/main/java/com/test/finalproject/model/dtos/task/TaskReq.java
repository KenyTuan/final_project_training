package com.test.finalproject.model.dtos.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class TaskReq {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "User id is required")
    private Integer userId;

}
