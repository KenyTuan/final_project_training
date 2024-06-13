package com.test.finalproject.controller;

import com.test.finalproject.AbstractTest;
import com.test.finalproject.constants.ApiEndpoints;
import com.test.finalproject.exception.BadRequestException;
import com.test.finalproject.model.dtos.auth.AuthReq;
import com.test.finalproject.model.dtos.auth.AuthRes;
import com.test.finalproject.model.dtos.auth.RegisterReq;
import com.test.finalproject.service.AuthService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AuthControllerTest extends AbstractTest {

    private static final String END_POINT = ApiEndpoints.PREFIX + ApiEndpoints.ACC_V1;

    @Mock
    private AuthService authService;

    private AuthReq authReq;
    private AuthRes res;
    private RegisterReq registerReq;

    @Override
    @Before
    public void setUp() {
        super.setUp();
        authReq = AuthReq.builder()
                .username("tuanvo123")
                .password("12345678")
                .build();

        res = new AuthRes(
                "token"
        );

        registerReq = RegisterReq.builder()
                .username("tuanvo312")
                .password("12345678")
                .email("tuanvo132@gmail.com")
                .firstName("Vo Thanh")
                .lastName("Tuan")
                .build();
    }

    //===================TEST_SUCCESS========================
    @Test
    public void success_LoginUser() throws Exception {

        when(authService.login(Mockito.any(AuthReq.class))).thenReturn(res);

        String inputJson = super.mapToJson(authReq);
        mvc.perform(MockMvcRequestBuilders.post(END_POINT + "/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    public void success_RegisterUser() throws Exception {
        when(authService.register(Mockito.any(RegisterReq.class))).thenReturn(res);

        String inputJson = super.mapToJson(registerReq);
        mvc.perform(MockMvcRequestBuilders.post(END_POINT + "/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists());
    }

    //===================Test Handle Exception===============
    //-Login User
    @Test
    public void handleException_InvalidUsername_LoginUser() throws Exception {
        authReq.setUsername("tuan");

        doThrow(new BadRequestException("400","Username Invalid!")).when(authService).login(Mockito.any(AuthReq.class));

        String inputJson = super.mapToJson(authReq);
        mvc.perform(MockMvcRequestBuilders.post(END_POINT + "/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputJson))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Username Invalid!"));
    }

    @Test
    public void handleException_InvalidPassword_LoginUser() throws Exception {
        authReq.setPassword("123456");

        doThrow(new BadRequestException("400","Password Invalid!")).when(authService).login(Mockito.any(AuthReq.class));

        String inputJson = super.mapToJson(authReq);
        mvc.perform(MockMvcRequestBuilders.post(END_POINT + "/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputJson))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Password Invalid!"));
    }

    @Test
    public void handleException_NotMatchPassword_LoginUser() throws Exception {
        authReq.setPassword("12345679");

        doThrow(new BadCredentialsException("UNAUTHORIZED: Invalid username or password")).when(authService).login(Mockito.any(AuthReq.class));

        String inputJson = super.mapToJson(authReq);
        mvc.perform(MockMvcRequestBuilders.post(END_POINT + "/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputJson))
                .andExpect(status().isUnauthorized())
                .andExpect(result -> assertInstanceOf(BadCredentialsException.class, result.getResolvedException()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("401"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("UNAUTHORIZED: Invalid username or password"));
    }

    //-Register User
    @Test
    public void handleException_InvalidUsername_RegisterUser() throws Exception {
        registerReq.setUsername("tuan");

        doThrow(new BadRequestException("400","Username Invalid!"))
                .when(authService).register(Mockito.any(RegisterReq.class));

        String inputJson = super.mapToJson(registerReq);
        mvc.perform(MockMvcRequestBuilders.post(END_POINT + "/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputJson))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Username Invalid!"));
    }

    @Test
    public void handleException_InvalidPassword_RegisterUser() throws Exception {
        registerReq.setPassword("12345");

        doThrow(new BadRequestException("400","Password Invalid!"))
                .when(authService).register(Mockito.any(RegisterReq.class));

        String inputJson = super.mapToJson(registerReq);
        mvc.perform(MockMvcRequestBuilders.post(END_POINT + "/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputJson))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Password Invalid!"));
    }

    @Test
    public void handleException_InvalidEmail_RegisterUser() throws Exception {
        registerReq.setEmail("testgmail.com");

        doThrow(new BadRequestException("400","Email Invalid!"))
                .when(authService).register(Mockito.any(RegisterReq.class));

        String inputJson = super.mapToJson(registerReq);
        mvc.perform(MockMvcRequestBuilders.post(END_POINT + "/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputJson))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Email Invalid!"));
    }

    @Test
    public void handleException_InvalidFirstName_RegisterUser() throws Exception {
        registerReq.setFirstName(" ");

        doThrow(new BadRequestException("400","First Name is required"))
                .when(authService).register(Mockito.any(RegisterReq.class));

        String inputJson = super.mapToJson(registerReq);
        mvc.perform(MockMvcRequestBuilders.post(END_POINT + "/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputJson))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("First Name is required"));
    }

    @Test
    public void handleException_InvalidLastName_RegisterUser() throws Exception {
        registerReq.setLastName(" ");

        doThrow(new BadRequestException("400","Last Name is required"))
                .when(authService).register(Mockito.any(RegisterReq.class));

        String inputJson = super.mapToJson(registerReq);
        mvc.perform(MockMvcRequestBuilders.post(END_POINT + "/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputJson))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Last Name is required"));
    }

}
