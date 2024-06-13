package com.test.finalproject.service;

import com.test.finalproject.model.dtos.taskDetail.TaskDetailReq;
import com.test.finalproject.model.dtos.taskDetail.TaskDetailRes;

public interface TaskDetailService {

    TaskDetailRes addTaskDetail(TaskDetailReq req);

    TaskDetailRes updateTaskDetail(TaskDetailReq req, int id);

    void deleteTaskDetail(int id);

}
