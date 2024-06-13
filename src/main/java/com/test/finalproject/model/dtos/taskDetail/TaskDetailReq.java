package com.test.finalproject.model.dtos.taskDetail;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class TaskDetailReq {

    @NotNull(message = "Task id is required")
    private Integer taskId;

    @NotBlank(message = "Name is required")
    private String name;

}
