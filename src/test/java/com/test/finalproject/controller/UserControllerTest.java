package com.test.finalproject.controller;

import com.test.finalproject.AbstractTest;
import com.test.finalproject.constants.ApiEndpoints;
import com.test.finalproject.constants.MessageException;
import com.test.finalproject.entity.User;
import com.test.finalproject.enums.AccountStatus;
import com.test.finalproject.exception.NotFoundException;
import com.test.finalproject.model.dtos.auth.AuthReq;
import com.test.finalproject.model.dtos.user.UserRes;
import com.test.finalproject.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerTest extends AbstractTest {
    private static final String END_POINT = ApiEndpoints.PREFIX + ApiEndpoints.USER_V1;

    @MockBean
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User user;
    private UserRes userRes;

    @Override
    @Before
    public void setUp() {
        super.setUp();

        user = User.builder()
                .id(1)
                .username("tuanvo123")
                .email("test@test.com")
                .password("$2a$10$z7G...")
                .firstName("Vo")
                .lastName("Tuan")
                .status(AccountStatus.ACTIVE)
                .build();

        userRes = new UserRes(1,"votuan123","tuan","vo","vothanhtuan069@gmail.com",AccountStatus.ACTIVE,Collections.emptyList());
    }

    @Test
    public void success_GetAllUser() throws Exception {
        when(userService.getAll()).thenReturn(Collections.singletonList(userRes));

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
        doThrow(new NotFoundException(MessageException.NOT_FOUND_USER)).when(userService).updateUserLock(anyInt());

        mvc.perform(MockMvcRequestBuilders.patch(END_POINT + "/122/locked")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("404"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(MessageException.NOT_FOUND_USER));
    }

}
