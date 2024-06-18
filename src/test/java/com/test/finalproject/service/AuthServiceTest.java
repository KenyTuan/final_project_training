package com.test.finalproject.service;

import com.test.finalproject.config.JwtUtil;
import com.test.finalproject.constants.MessageException;
import com.test.finalproject.entity.User;
import com.test.finalproject.entity.PasswordRestToken;
import com.test.finalproject.enums.AccountStatus;
import com.test.finalproject.exception.BadRequestException;
import com.test.finalproject.exception.NotFoundException;
import com.test.finalproject.model.dtos.auth.*;
import com.test.finalproject.repository.UserRepository;
import com.test.finalproject.repository.VerifyEmailRepository;
import com.test.finalproject.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private VerifyEmailRepository verifyEmailRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private MailService mailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    @InjectMocks
    private User user;

    @InjectMocks
    private PasswordRestToken passwordRestToken;

    @InjectMocks
    String token;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .id(1)
                .username("votuan123")
                .email("votuan123@gmail.com")
                .password("$2a$10$z7G...")
                .status(AccountStatus.ACTIVE)
                .firstName("vo")
                .lastName("tuan")
                .build();

        token = UUID.randomUUID().toString();

        passwordRestToken = PasswordRestToken.builder()
                .token(UUID.randomUUID().toString())
                .expiryDate(new Timestamp(System.currentTimeMillis() + 60 * 15 *1000))
                .user(user)
                .build();
    }

    //==================Test_Login=============================
    @Test
    public void testLogin_WhenSuccess() {
        AuthReq authReq = AuthReq.builder().username("votuan123").password("123@L.quy5401").build();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtil.generateToken(any(User.class))).thenReturn("Bearer token");
        AuthRes authRes = authService.login(authReq);

        assertThat(authRes).isNotNull();
        assertEquals(authRes.token(),"Bearer token");
        verify(userRepository,times(1)).findByUsername(anyString());
        verify(passwordEncoder,times(1)).matches(anyString(),anyString());
        verify(jwtUtil,times(1)).generateToken(any(User.class));
    }

    @Test
    public void testLogin_WhenUserNotFound() {
        AuthReq authReq = AuthReq.builder().username("votuan123").password("123@L.quy5401").build();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(authReq))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(MessageException.NOT_FOUND_USER);
        verify(userRepository,times(1)).findByUsername(anyString());
        verify(passwordEncoder,never()).matches(anyString(),anyString());
        verify(jwtUtil,never()).generateToken(any(User.class));
    }

    @Test
    public void testLogin_WhenNotMatchPassword() {
        AuthReq authReq = AuthReq.builder().username("votuan123").password("123@L.quy5401").build();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(authReq))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining(MessageException.NOT_MATCH_PASSWORD);
        verify(userRepository,times(1)).findByUsername(anyString());
        verify(passwordEncoder,times(1)).matches(anyString(), anyString());
        verify(jwtUtil,never()).generateToken(any(User.class));
    }

    @Test
    public void testLogin_WhenAccountLocked() {
        user.setStatus(AccountStatus.LOCKED);
        AuthReq authReq = AuthReq.builder().username("votuan123").password("123@L.quy5401").build();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        assertThatThrownBy(() -> authService.login(authReq))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining(MessageException.ACCOUNT_LOCKED);
        verify(userRepository,times(1)).findByUsername(anyString());
        verify(passwordEncoder,times(1)).matches(anyString(), anyString());
        verify(jwtUtil,never()).generateToken(any(User.class));
    }

    //=================Test_Register======================
    @Test
    public void testRegister_WhenSuccess() {
        RegisterReq registerReq = RegisterReq
                .builder()
                .username("votuan154")
                .email("votuan154@gmail.com")
                .password("@123Password")
                .firstName("vo")
                .lastName("tuan")
                .build();

        lenient().when(userRepository.existsByUsernameOrEmail(anyString(), anyString())).thenReturn(false);

        lenient().when(userRepository.save(any(User.class))).thenReturn(user);

        lenient().when(jwtUtil.generateToken(any(User.class))).thenReturn("Bearer token");

        AuthRes authRes = authService.register(registerReq);

        assertThat(authRes).isNotNull();

        assertEquals(authRes.token(),"Bearer token");

        verify(userRepository,times(1)).existsByUsernameOrEmail(anyString(), anyString());
        verify(userRepository,times(1)).save(any(User.class));
        verify(jwtUtil,times(1)).generateToken(any(User.class));
    }

    @Test
    public void testRegister_WhenEmailOrUsernameAlreadyExists() {
        RegisterReq registerReq = RegisterReq
                .builder()
                .username("votuan564")
                .email("votuan564@gmail.com")
                .password("@123Password")
                .firstName("vo")
                .lastName("tuan")
                .build();

        when(userRepository.existsByUsernameOrEmail(anyString(), anyString())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerReq))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(MessageException.ALREADY_EXIST_USERNAME_OR_EMAIL);
        verify(userRepository,times(1)).existsByUsernameOrEmail(anyString(), anyString());
        verify(userRepository,never()).save(any(User.class));
    }

    //=================Test_CreateVerify==================
    @Test
    public void testCreatePasswordRestToken_WhenSuccess() {
        String email = "votuan123@gmail.com";

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(verifyEmailRepository.save(any(PasswordRestToken.class))).thenReturn(passwordRestToken);

        authService.createPasswordRestToken(email);

        verify(userRepository, times(1)).findByEmail(anyString());
        verify(verifyEmailRepository, times(1)).save(any(PasswordRestToken.class));
    }

    @Test
    public void testCreatePasswordRestToken_WhenUserNotFound() {
        String email = "notfound@gmail.com";

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.createPasswordRestToken(email))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(MessageException.NOT_FOUND_USER);
        verify(userRepository,times(1)).findByEmail(anyString());
        verify(verifyEmailRepository,never()).save(any(PasswordRestToken.class));
    }

    //===================Test_Confirm_Verification======
    @Test
    public void testConfirmPasswordRestToken_WhenSuccess() {
        String token = passwordRestToken.getToken();
        String email = passwordRestToken.getUser().getEmail();

        when(verifyEmailRepository.findByUserEmailAndToken(anyString(), anyString())).thenReturn(Optional.of(passwordRestToken));
        authService.confirmPasswordRestToken(token, email);

        verify(verifyEmailRepository, times(1)).findByUserEmailAndToken(anyString(), anyString());
    }

    @Test
    public void testConfirmPasswordRestToken_WhenTokenVerifyNotFound() {
        String token = "invalidToken";
        String email = "votuan123@gmail.com";

        when(verifyEmailRepository.findByUserEmailAndToken(anyString(), anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.confirmPasswordRestToken(token,email))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(MessageException.NOT_FOUND_TOKEN_VERIFY);
        verify(verifyEmailRepository,times(1)).findByUserEmailAndToken(anyString(), anyString());
    }

    @Test
    public void testConfirmPasswordRestToken_WhenTokenExpired() {
        String token = passwordRestToken.getToken();
        String email = passwordRestToken.getUser().getEmail();
        passwordRestToken.setExpiryDate(new Timestamp(System.currentTimeMillis() - 15*60*1000));

        when(verifyEmailRepository.findByUserEmailAndToken(anyString(), anyString())).thenReturn(Optional.of(passwordRestToken));

        assertThatThrownBy(() -> authService.confirmPasswordRestToken(token,email))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining(MessageException.TOKEN_EXPIRED);
        verify(verifyEmailRepository,times(1)).findByUserEmailAndToken(anyString(), anyString());
    }
}