package com.test.finalproject.model.converter;

import com.test.finalproject.entity.Task;
import com.test.finalproject.entity.User;
import com.test.finalproject.model.dtos.task.TaskReq;
import com.test.finalproject.model.dtos.task.TaskRes;

import java.util.Collections;
import java.util.List;

public class TaskDtoConverter {

    public static List<TaskRes> toModelList(List<Task> tasks) {
        return tasks.stream()
                .map(TaskDtoConverter::toResponse)
                .toList();
    }

    public static TaskRes toResponse(Task task) {
        return new TaskRes(
                task.getId(),
                task.getName(),
                task.getCompleteDate(),
                task.getStatus(),
                task.getTaskDetails() != null?TaskDetailDtoConverter.toModelList(task.getTaskDetails()) : Collections.emptyList()
        );
    }

    public static Task toEntity(TaskReq req) {
        return Task.builder()
                .name(req.getName())
                .user(User.builder().id(req.getUserId()).build())
                .build();
    }
}
