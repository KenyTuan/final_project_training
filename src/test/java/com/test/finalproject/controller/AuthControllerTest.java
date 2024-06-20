package com.test.finalproject.controller;

import com.test.finalproject.AbstractTest;
import com.test.finalproject.config.JwtUtil;
import com.test.finalproject.constants.ApiEndpoints;
import com.test.finalproject.constants.MessageException;
import com.test.finalproject.exception.BadRequestException;
import com.test.finalproject.exception.NotFoundException;
import com.test.finalproject.model.dtos.auth.*;
import com.test.finalproject.service.AuthService;
import com.test.finalproject.service.MailService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AuthControllerTest extends AbstractTest {

    private static final String END_POINT = ApiEndpoints.PREFIX + ApiEndpoints.ACC_V1;

    @MockBean
    private AuthService authService;

    @MockBean
    private MailService mailService;

    @MockBean
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    private AuthReq authReq;
    private AuthRes res;
    private RegisterReq registerReq;
    private RequestForgot requestForgot;

    @Override
    @Before
    public void setUp() {
        super.setUp();

        authReq = AuthReq.builder()
                .username("tuanvo123")
                .password("123@L.quy5401")
                .build();

        res = new AuthRes(
                "token"
        );

        registerReq = RegisterReq.builder()
                .username("tuanvo312")
                .password("123@L.quy5401")
                .email("tuanvo132@gmail.com")
                .firstName("Vo Thanh")
                .lastName("Tuan")
                .build();

        requestForgot = RequestForgot.builder()
                .token("token")
                .email("tuanvo132@gmail.com")
                .password("123@L.quy5401")
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
                .andExpect(status().isOk())
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

    @Test
    public void success_RequestForgotPassword() throws Exception {

        mvc.perform(MockMvcRequestBuilders.post(END_POINT + "/forget-password/request")
                        .param("email", "test@test.com")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated());
    }

    @Test
    public void success_ForgotPassword() throws Exception {

        String inputJson = super.mapToJson(requestForgot);

        mvc.perform(MockMvcRequestBuilders.patch(END_POINT + "/forget-password")
                        .content(inputJson)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    //===================Test Handle Exception===============
    //-Login User
    @Test
    public void handleException_InvalidUsername_LoginUser() throws Exception {
        authReq.setUsername("tuan");

        doThrow(new BadRequestException(MessageException.INVALID_USERNAME)).when(authService).login(Mockito.any(AuthReq.class));

        String inputJson = super.mapToJson(authReq);
        mvc.perform(MockMvcRequestBuilders.post(END_POINT + "/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputJson))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(MessageException.INVALID_USERNAME));
    }

    @Test
    public void handleException_InvalidPassword_LoginUser() throws Exception {
        authReq.setPassword("123Tuan");

        doThrow(new BadRequestException(MessageException.INVALID_PASSWORD)).when(authService).login(Mockito.any(AuthReq.class));

        String inputJson = super.mapToJson(authReq);
        mvc.perform(MockMvcRequestBuilders.post(END_POINT + "/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputJson))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(MessageException.INVALID_PASSWORD));
    }

    @Test
    public void handleException_NotMatchPassword_LoginUser() throws Exception {
        authReq.setPassword("123@gamil1S");

        doThrow(new BadRequestException(MessageException.NOT_MATCH_PASSWORD)).when(authService).login(Mockito.any(AuthReq.class));

        String inputJson = super.mapToJson(authReq);
        mvc.perform(MockMvcRequestBuilders.post(END_POINT + "/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputJson))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(BadRequestException.class, result.getResolvedException()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(MessageException.NOT_MATCH_PASSWORD));
    }

    @Test
    public void handleException_WhenAccountLocked_LoginUser() throws Exception {
        doThrow(new BadRequestException(MessageException.ACCOUNT_LOCKED)).when(authService).login(Mockito.any(AuthReq.class));

        String inputJson = super.mapToJson(authReq);
        mvc.perform(MockMvcRequestBuilders.post(END_POINT + "/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputJson))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(BadRequestException.class, result.getResolvedException()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(MessageException.ACCOUNT_LOCKED));
    }

    //-Register User
    @Test
    public void handleException_InvalidUsername_RegisterUser() throws Exception {
        registerReq.setUsername("tuan");

        doThrow(new BadRequestException(MessageException.INVALID_USERNAME))
                .when(authService).register(Mockito.any(RegisterReq.class));

        String inputJson = super.mapToJson(registerReq);
        mvc.perform(MockMvcRequestBuilders.post(END_POINT + "/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputJson))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(MessageException.INVALID_USERNAME));
    }

    @Test
    public void handleException_InvalidPassword_RegisterUser() throws Exception {
        registerReq.setPassword("123@gaaasm");

        doThrow(new BadRequestException(MessageException.INVALID_PASSWORD))
                .when(authService).register(Mockito.any(RegisterReq.class));

        String inputJson = super.mapToJson(registerReq);
        mvc.perform(MockMvcRequestBuilders.post(END_POINT + "/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputJson))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(MessageException.INVALID_PASSWORD));
    }

    @Test
    public void handleException_InvalidEmail_RegisterUser() throws Exception {
        registerReq.setEmail("testgmail.com");

        doThrow(new BadRequestException(MessageException.INVALID_EMAIL))
                .when(authService).register(Mockito.any(RegisterReq.class));

        String inputJson = super.mapToJson(registerReq);
        mvc.perform(MockMvcRequestBuilders.post(END_POINT + "/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputJson))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(MessageException.INVALID_EMAIL));
    }

    @Test
    public void handleException_InvalidFirstName_RegisterUser() throws Exception {
        registerReq.setFirstName(" ");

        doThrow(new BadRequestException(MessageException.REQUIRED_FIRST_NAME))
                .when(authService).register(Mockito.any(RegisterReq.class));

        String inputJson = super.mapToJson(registerReq);
        mvc.perform(MockMvcRequestBuilders.post(END_POINT + "/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputJson))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(MessageException.REQUIRED_FIRST_NAME));
    }

    @Test
    public void handleException_InvalidLastName_RegisterUser() throws Exception {
        registerReq.setLastName(" ");

        doThrow(new BadRequestException(MessageException.REQUIRED_LAST_NAME))
                .when(authService).register(Mockito.any(RegisterReq.class));

        String inputJson = super.mapToJson(registerReq);
        mvc.perform(MockMvcRequestBuilders.post(END_POINT + "/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputJson))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(MessageException.REQUIRED_LAST_NAME));
    }

    @Test
    public void handleException_InvalidPassword_ForgotPassword() throws Exception {
        requestForgot.setPassword("123@SSSS");

        doThrow(new BadRequestException(MessageException.INVALID_PASSWORD))
                .when(authService).forgotPassword(Mockito.any(RequestForgot.class));

        String inputJson = super.mapToJson(requestForgot);
        mvc.perform(MockMvcRequestBuilders.patch(END_POINT + "/forget-password")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputJson))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(MessageException.INVALID_PASSWORD));
    }

    @Test
    public void handleException_InvalidEmail_ForgotPassword() throws Exception {
        requestForgot.setEmail("vothanhgmail.com");

        doThrow(new BadRequestException(MessageException.INVALID_EMAIL))
                .when(authService).forgotPassword(Mockito.any(RequestForgot.class));

        String inputJson = super.mapToJson(requestForgot);
        mvc.perform(MockMvcRequestBuilders.patch(END_POINT + "/forget-password")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputJson))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(MessageException.INVALID_EMAIL));
    }

    @Test
    public void handleException_RequiredToken_ForgotPassword() throws Exception {
        requestForgot.setToken("");

        doThrow(new BadRequestException(MessageException.REQUIRED_TOKEN))
                .when(authService).forgotPassword(Mockito.any(RequestForgot.class));

        String inputJson = super.mapToJson(requestForgot);
        mvc.perform(MockMvcRequestBuilders.patch(END_POINT + "/forget-password")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputJson))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(MessageException.REQUIRED_TOKEN));
    }

    @Test
    public void handleException_NotFoundUserWithEmail_ForgotPassword() throws Exception {
        doThrow(new NotFoundException(MessageException.NOT_FOUND_USER))
                .when(authService).forgotPassword(any(RequestForgot.class));

        String inputJson = super.mapToJson(requestForgot);
        mvc.perform(MockMvcRequestBuilders.patch(END_POINT + "/forget-password")
                        .content(inputJson)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("404"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(MessageException.NOT_FOUND_USER));
    }

    @Test
    public void handleException_NotFoundUserWithToken_ForgotPassword() throws Exception {

        doThrow(new NotFoundException(MessageException.NOT_FOUND_USER))
                .when(authService).forgotPassword(any(RequestForgot.class));

        String inputJson = super.mapToJson(requestForgot);
        mvc.perform(MockMvcRequestBuilders.patch(END_POINT + "/forget-password")
                        .content(inputJson)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("404"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(MessageException.NOT_FOUND_USER));
    }

    @Test
    public void handleException_WhenTokenExpired_ForgotPassword() throws Exception {
        doThrow(new BadRequestException(MessageException.TOKEN_EXPIRED))
                .when(authService).forgotPassword(any(RequestForgot.class));

        String inputJson = super.mapToJson(requestForgot);
        mvc.perform(MockMvcRequestBuilders.patch(END_POINT + "/forget-password")
                        .content(inputJson)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(BadRequestException.class, result.getResolvedException()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(MessageException.TOKEN_EXPIRED));
    }

    @Test
    public void handleException_NotFoundUser_RequestForgotPassword() throws Exception {
        doThrow(new NotFoundException(MessageException.NOT_FOUND_USER))
                .when(authService).requestForgotPassword(anyString());

        mvc.perform(MockMvcRequestBuilders.post(END_POINT + "/forget-password/request")
                        .param("email", "test@test.com")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("404"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(MessageException.NOT_FOUND_USER));
    }
}
