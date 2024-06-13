package com.test.finalproject.controller;


import com.test.finalproject.AbstractTest;
import com.test.finalproject.constants.ApiEndpoints;
import com.test.finalproject.enums.ProgressStatus;
import com.test.finalproject.exception.BadRequestException;
import com.test.finalproject.exception.NotFoundException;
import com.test.finalproject.model.dtos.task.TaskReq;
import com.test.finalproject.model.dtos.task.TaskRes;
import com.test.finalproject.service.TaskService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.sql.Date;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TaskControllerTest extends AbstractTest {

    private static final String END_POINT = ApiEndpoints.PREFIX + ApiEndpoints.TASK_V1;

    @Mock
    private TaskService taskService;

    private TaskRes res;
    private TaskReq taskReq;

    @Override
    @Before
    public void setUp() {
        super.setUp();

        res = new TaskRes(
                1,
                "Feature Manager User",
                null,
                ProgressStatus.TODO,
                Collections.emptyList()
        );

        taskReq = TaskReq.builder()
                .name("Feature Manager Product")
                .userId(1)
                .build();
    }

    //========================TEST_SUCCESS===========================
    @Test
    public void success_AddTask() throws Exception {
        res = new TaskRes(2,taskReq.getName(), null, ProgressStatus.TODO, Collections.emptyList());

        when(taskService.addTask(Mockito.any(TaskReq.class))).thenReturn(res);

        String inputJson = super.mapToJson(taskReq);
        mvc.perform(MockMvcRequestBuilders.post(END_POINT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputJson))
                .andExpect(status().isCreated())
                .andExpect(content().json(mapToJson(res)));
    }

    @Test
    public void success_GetTaskById() throws Exception {

        when(taskService.getTask(Mockito.anyInt())).thenReturn(res);

        String inputJson = super.mapToJson(taskReq);
        mvc.perform(MockMvcRequestBuilders.get(END_POINT+ "/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputJson))
                .andExpect(status().isOk())
                .andExpect(content().json(mapToJson(res)));
    }

    @Test
    public void success_GetAllTasks() throws Exception {

        when(taskService.getTasks());

        String inputJson = super.mapToJson(taskReq);
        mvc.perform(MockMvcRequestBuilders.get(END_POINT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputJson))
                .andExpect(status().isOk());
    }

    @Test
    public void success_UpdateTask() throws Exception {
        res = new TaskRes(1,taskReq.getName(), null, ProgressStatus.TODO, Collections.emptyList());

        when(taskService.updateTask(any(TaskReq.class),anyInt()))
                .thenReturn(res);

        String inputJson = super.mapToJson(taskReq);
        mvc.perform(MockMvcRequestBuilders.put(END_POINT + "/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputJson))
                .andExpect(status().isAccepted())
                .andExpect(content().json(mapToJson(res)));
    }

    @Test
    public void success_UpdateCompleteTask() throws Exception {
        res = new TaskRes(1,res.name(),new Date(System.currentTimeMillis()),
                ProgressStatus.COMPLETE, Collections.emptyList());

        when(taskService.updateTaskCompleted(anyInt()))
                .thenReturn(res);

        mvc.perform(MockMvcRequestBuilders.patch(END_POINT + "/1/complete")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        )
                .andExpect(status().isOk())
                .andExpect(content().json(mapToJson(res)));
    }

    @Test
    public void success_DeleteTask() throws Exception {

        mvc.perform(MockMvcRequestBuilders.delete(END_POINT + "/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isNoContent());
    }

    //====================Test Handle Exception======================

    @Test
    public void handleException_InvalidName_RequestTask() throws Exception {
        taskReq.setName(" ");
        doThrow(new BadRequestException("400","Name is required"))
                .when(taskService).addTask(Mockito.any(TaskReq.class));

        String inputJson = super.mapToJson(taskReq);
        mvc.perform(MockMvcRequestBuilders.post(END_POINT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputJson))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Name is required"));
    }

    @Test
    public void handleException_InvalidUserId_RequestTask() throws Exception {
        taskReq.setUserId(null);
        doThrow(new BadRequestException("400","User id is required"))
                .when(taskService).addTask(Mockito.any(TaskReq.class));

        String inputJson = super.mapToJson(taskReq);
        mvc.perform(MockMvcRequestBuilders.post(END_POINT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputJson))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User id is required"));
    }

    //===================Handle_Exception_NotFound=================
    @Test
    public void handleException_NotFoundUser_RequestAddTask() throws Exception {
        taskReq.setUserId(0);
        doThrow(new NotFoundException("404","User not found"))
                .when(taskService).addTask(Mockito.any(TaskReq.class));

        String inputJson = super.mapToJson(taskReq);
        mvc.perform(MockMvcRequestBuilders.post(END_POINT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputJson))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("404"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User not found"));
    }

    @Test
    public void handleException_NotFoundUser_RequestUpdateTask() throws Exception {
        taskReq.setUserId(0);
        doThrow(new NotFoundException("404","User not found"))
                .when(taskService).updateTask(Mockito.any(TaskReq.class),anyInt());

        String inputJson = super.mapToJson(taskReq);
        mvc.perform(MockMvcRequestBuilders.put(END_POINT + "/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputJson))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("404"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User not found"));
    }

    @Test
    public void handleException_NotFoundTask_RequestUpdateTask() throws Exception {
        taskReq.setUserId(1);
        doThrow(new NotFoundException("404","Task not found"))
                .when(taskService).updateTask(Mockito.any(TaskReq.class),anyInt());

        String inputJson = super.mapToJson(taskReq);
        mvc.perform(MockMvcRequestBuilders.put(END_POINT + "/100")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputJson))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("404"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Task not found"));
    }

    @Test
    public void handleException_NotFoundTask_RequestDeleteTask() throws Exception {
        doThrow(new NotFoundException("404","Task not found"))
                .when(taskService).deleteTask(anyInt());

        String inputJson = super.mapToJson(taskReq);
        mvc.perform(MockMvcRequestBuilders.delete(END_POINT + "/100")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputJson))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("404"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Task not found"));
    }

    @Test
    public void handleException_NotFoundTask_RequestGetTaskById() throws Exception {
        doThrow(new NotFoundException("404","Task not found"))
                .when(taskService).getTask(anyInt());

        String inputJson = super.mapToJson(taskReq);
        mvc.perform(MockMvcRequestBuilders.get(END_POINT + "/100")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputJson))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("404"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Task not found"));
    }

    @Test
    public void handleException_NotFoundTask_RequestUpdateCompleteTask() throws Exception {
        doThrow(new NotFoundException("404","Task not found"))
                .when(taskService).updateTaskCompleted(anyInt());

        String inputJson = super.mapToJson(taskReq);
        mvc.perform(MockMvcRequestBuilders.patch(END_POINT + "/100/complete")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputJson))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("404"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Task not found"));
    }
}
