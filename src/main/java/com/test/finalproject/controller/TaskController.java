package com.test.finalproject.controller;

import com.test.finalproject.constants.ApiEndpoints;
import com.test.finalproject.model.dtos.task.TaskReq;
import com.test.finalproject.model.dtos.task.TaskRes;
import com.test.finalproject.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiEndpoints.PREFIX)
public class TaskController {
    private final TaskService taskService;

    @GetMapping(ApiEndpoints.TASK_V1)
    public List<TaskRes> getTaskList(){
        return taskService.getTasks();
    }

    @GetMapping(ApiEndpoints.TASK_V1 + "/{id}")
    public TaskRes getTaskById(@PathVariable int id){
        return taskService.getTask(id);
    }

    @PostMapping(ApiEndpoints.TASK_V1)
    @ResponseStatus(HttpStatus.CREATED)
    public TaskRes createTask(@RequestBody @Valid TaskReq taskReq){
        return taskService.addTask(taskReq);
    }

    @PutMapping(ApiEndpoints.TASK_V1 + "/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public TaskRes updateTask(
            @RequestBody @Valid TaskReq taskReq,
            @PathVariable int id){
        return taskService.updateTask(taskReq,id);
    }

    @PatchMapping(ApiEndpoints.TASK_V1 + "/{id}/complete")
    @ResponseStatus(HttpStatus.OK)
    public TaskRes updateTaskComplete(@PathVariable int id){
        return taskService.updateTaskCompleted(id);
    }


    @DeleteMapping(ApiEndpoints.TASK_V1 + "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable int id){
        taskService.deleteTask(id);
    }
}
