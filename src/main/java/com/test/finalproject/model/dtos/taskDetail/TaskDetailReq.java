package com.test.finalproject.model.dtos.taskDetail;

import com.test.finalproject.constants.MessageException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class TaskDetailReq {

    @NotNull(message = MessageException.REQUIRED_TASK_ID)
    private Integer taskId;

    @NotBlank(message = MessageException.REQUIRED_NAME)
    private String name;

}
