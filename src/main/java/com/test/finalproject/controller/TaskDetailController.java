package com.test.finalproject.controller;

import com.test.finalproject.constants.ApiEndpoints;
import com.test.finalproject.model.dtos.taskDetail.TaskDetailReq;
import com.test.finalproject.model.dtos.taskDetail.TaskDetailRes;
import com.test.finalproject.service.TaskDetailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiEndpoints.PREFIX)
public class TaskDetailController {

    private final TaskDetailService taskDetailService;

    @PostMapping(ApiEndpoints.TASK_DETAIL_V1)
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDetailRes addTaskDetail(@RequestBody @Valid TaskDetailReq taskDetailReq) {
        return taskDetailService.addTaskDetail(taskDetailReq);
    }

    @PutMapping(ApiEndpoints.TASK_DETAIL_V1 + "/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public TaskDetailRes updateTaskDetail(@RequestBody @Valid TaskDetailReq taskDetailReq,
                                          @PathVariable int id) {
        return taskDetailService.updateTaskDetail(taskDetailReq,id);
    }

    @DeleteMapping(ApiEndpoints.TASK_DETAIL_V1 + "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTaskDetail(@PathVariable int id) {
        taskDetailService.deleteTaskDetail(id);
    }
}
