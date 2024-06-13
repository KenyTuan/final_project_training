package com.test.finalproject.model.dtos.task;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.test.finalproject.enums.ProgressStatus;
import com.test.finalproject.model.dtos.taskDetail.TaskDetailRes;

import java.util.Date;
import java.util.List;

public record TaskRes(
        Integer id,
        String name,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
        Date completeDate,
        ProgressStatus status,
        List<TaskDetailRes> TaskDetails
) {
}
