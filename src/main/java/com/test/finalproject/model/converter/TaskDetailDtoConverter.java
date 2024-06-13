package com.test.finalproject.model.converter;

import com.test.finalproject.entity.TaskDetail;
import com.test.finalproject.model.dtos.taskDetail.TaskDetailReq;
import com.test.finalproject.model.dtos.taskDetail.TaskDetailRes;

import java.util.List;

public class TaskDetailDtoConverter {


    public static List<TaskDetailRes> toModelList(List<TaskDetail> taskDetails) {
        return taskDetails.stream()
                .map(TaskDetailDtoConverter::toResponse)
                .toList();
    }

    public static TaskDetailRes toResponse(TaskDetail taskDetail) {
        return new TaskDetailRes(
                taskDetail.getId(),
                taskDetail.getName()
        );
    }

    public static List<TaskDetail> toListEntity(List<TaskDetailReq> taskDetail) {
        return taskDetail.stream()
                .map(TaskDetailDtoConverter::toEntity)
                .toList();
    }

    public static TaskDetail toEntity(TaskDetailReq taskDetailReq) {
        return TaskDetail.builder()
                .name(taskDetailReq.getName())
                .build();
    }
}
