package com.test.finalproject.controller;

import com.test.finalproject.AbstractTest;
import com.test.finalproject.config.JwtUtil;
import com.test.finalproject.constants.ApiEndpoints;
import com.test.finalproject.constants.MessageException;
import com.test.finalproject.entity.User;
import com.test.finalproject.entity.PasswordRestToken;
import com.test.finalproject.enums.AccountStatus;
import com.test.finalproject.exception.BadRequestException;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.UUID;

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
    private User user;
    private PasswordRestToken passwordRestToken;

    @Override
    @Before
    public void setUp() {
        super.setUp();
        user = User.builder()
                .id(1)
                .username("tuanvo123")
                .email("test@test.com")
                .password("$2a$10$z7G..." )
                .firstName("Vo")
                .lastName("Tuan")
                .status(AccountStatus.ACTIVE)
                .build();

        passwordRestToken = PasswordRestToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiryDate(new Timestamp(System.currentTimeMillis() + 15*60*1000))
                .id(1)
                .build();

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

    @Test
    public void success_CreateTokenVerify() throws Exception {

        mvc.perform(MockMvcRequestBuilders.post(END_POINT + "/generate-password-rest-token")
                        .param("email", user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated());
    }

    @Test
    public void success_ConfirmTokenVerify() throws Exception {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        params.put("email", Collections.singletonList(user.getEmail()));
        params.put("token", Collections.singletonList(passwordRestToken.getToken()));

        mvc.perform(MockMvcRequestBuilders.patch(END_POINT + "/confirm-password-rest-token")
                        .queryParams(params)
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
        user.setStatus(AccountStatus.LOCKED);

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

}
