package com.test.finalproject.controller;

import com.test.finalproject.AbstractTest;
import com.test.finalproject.constants.ApiEndpoints;
import com.test.finalproject.entity.TaskDetail;
import com.test.finalproject.exception.BadRequestException;
import com.test.finalproject.exception.NotFoundException;
import com.test.finalproject.model.dtos.auth.AuthReq;
import com.test.finalproject.model.dtos.taskDetail.TaskDetailReq;
import com.test.finalproject.model.dtos.taskDetail.TaskDetailRes;
import com.test.finalproject.service.TaskDetailService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TaskDetailControllerTest extends AbstractTest {
    private static final String END_POINT = ApiEndpoints.PREFIX + ApiEndpoints.TASK_DETAIL_V1;

    @Mock
    private TaskDetailService taskDetailService;

    @InjectMocks
    private TaskDetailController taskDetailController;
    private TaskDetailReq taskDetailReq;
    private TaskDetailRes res;

    @Override
    @Before
    public void setUp() {
        super.setUp();

        taskDetailReq = TaskDetailReq.builder()
                .taskId(1)
                .name("Feature SignUp")
                .build();

        res = new TaskDetailRes(
                1, "Feature Login"
        );

    }

    @Test
    public void success_AddTaskDetail() throws Exception {
        res = new TaskDetailRes(2, "Feature SignUp");
        when(taskDetailService.addTaskDetail(Mockito.any(TaskDetailReq.class))).thenReturn(res);

        String inputJson = super.mapToJson(taskDetailReq);
        mvc.perform(MockMvcRequestBuilders.post(END_POINT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json(mapToJson(res)));
    }

    @Test
    public void success_UpdateTaskDetail() throws Exception {
        res = new TaskDetailRes(1, "Feature SignUp");
        when(taskDetailService.updateTaskDetail(Mockito.any(TaskDetailReq.class),anyInt())).thenReturn(res);

        String inputJson = super.mapToJson(taskDetailReq);
        mvc.perform(MockMvcRequestBuilders.put(END_POINT + "/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputJson))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json(mapToJson(res)));
    }

    @Test
    public void success_DeleteTaskDetail() throws Exception {

        mvc.perform(MockMvcRequestBuilders.delete(END_POINT + "/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNoContent());
    }

    @Test
    public void handleException_NotFound_DeleteTaskDetail() throws Exception {
        doThrow(new NotFoundException("404","Task Detail not found"))
                .when(taskDetailService).deleteTaskDetail(anyInt());

        mvc.perform(MockMvcRequestBuilders.delete(END_POINT + "/2")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("404"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Task Detail not found"));
    }

    @Test
    public void handleException_NotFound_UpdateTaskDetail() throws Exception {
        doThrow(new NotFoundException("404","Task Detail not found"))
                .when(taskDetailService).updateTaskDetail(any(TaskDetailReq.class),anyInt());

        String inputJson = super.mapToJson(taskDetailReq);
        mvc.perform(MockMvcRequestBuilders.put(END_POINT + "/2")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputJson))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("404"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Task Detail not found"));
    }

    @Test
    public void handleException_NotFoundTask_UpdateTaskDetail() throws Exception {
        taskDetailReq.setTaskId(2);
        doThrow(new NotFoundException("404","Task not found"))
                .when(taskDetailService).updateTaskDetail(any(TaskDetailReq.class),anyInt());

        String inputJson = super.mapToJson(taskDetailReq);
        mvc.perform(MockMvcRequestBuilders.put(END_POINT + "/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputJson))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("404"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Task not found"));
    }

    @Test
    public void handleException_InvalidTaskId_TaskDetail() throws Exception {
        taskDetailReq.setTaskId(null);
        doThrow(new BadRequestException("400","Task id is required"))
                .when(taskDetailService).addTaskDetail(any(TaskDetailReq.class));

        String inputJson = super.mapToJson(taskDetailReq);
        mvc.perform(MockMvcRequestBuilders.post(END_POINT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputJson))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Task id is required"));
    }

    @Test
    public void handleException_InvalidName_TaskDetail() throws Exception {
        taskDetailReq.setName("");
        doThrow(new BadRequestException("400","Name is required"))
                .when(taskDetailService).addTaskDetail(any(TaskDetailReq.class));

        String inputJson = super.mapToJson(taskDetailReq);
        mvc.perform(MockMvcRequestBuilders.post(END_POINT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputJson))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Name is required"));
    }

}
