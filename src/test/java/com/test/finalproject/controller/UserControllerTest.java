package com.test.finalproject.controller;

import com.test.finalproject.AbstractTest;
import com.test.finalproject.constants.ApiEndpoints;
import com.test.finalproject.exception.NotFoundException;
import com.test.finalproject.model.dtos.auth.AuthReq;
import com.test.finalproject.model.dtos.user.UserRes;
import com.test.finalproject.service.UserService;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerTest extends AbstractTest {
    private static final String END_POINT = ApiEndpoints.PREFIX + ApiEndpoints.USER_V1;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Override
    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    public void success_GetAllUser() throws Exception {
        when(userService.getAll());

        mvc.perform(MockMvcRequestBuilders.get(END_POINT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void success_LockUser() throws Exception {

        mvc.perform(MockMvcRequestBuilders.patch(END_POINT + "/1/locked")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNoContent());
    }

    @Test
    public void handleException_NotFound_LockUser() throws Exception {
        doThrow(new NotFoundException("404","User not found")).when(userService).updateUserLock(anyInt());

        mvc.perform(MockMvcRequestBuilders.patch(END_POINT + "/122/locked")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("404"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User not found"));
    }

}
