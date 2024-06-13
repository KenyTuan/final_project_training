package com.test.finalproject.service;

import com.test.finalproject.model.dtos.task.TaskReq;
import com.test.finalproject.model.dtos.task.TaskRes;

import java.util.List;

public interface TaskService {

    List<TaskRes> getTasks();

    TaskRes getTask(int id);

    TaskRes addTask(TaskReq req);

    TaskRes updateTask(TaskReq req,int id);

    TaskRes updateTaskCompleted(int id);

    void deleteTask(int id);
}
