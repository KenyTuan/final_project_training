package com.test.finalproject.service;

import com.test.finalproject.config.JwtUtil;
import com.test.finalproject.constants.MessageException;
import com.test.finalproject.entity.User;
import com.test.finalproject.entity.VerifyEmail;
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
    private VerifyEmail verifyEmail;

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

        verifyEmail = VerifyEmail.builder()
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

        lenient().when(userRepository.existsByUsernameAndEmail(anyString(), anyString())).thenReturn(false);

        lenient().when(userRepository.save(any(User.class))).thenReturn(user);

        lenient().when(jwtUtil.generateToken(any(User.class))).thenReturn("Bearer token");

        AuthRes authRes = authService.register(registerReq);

        assertThat(authRes).isNotNull();

        assertEquals(authRes.token(),"Bearer token");

        verify(userRepository,times(1)).existsByUsernameAndEmail(anyString(), anyString());
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

        when(userRepository.existsByUsernameAndEmail(anyString(), anyString())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerReq))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(MessageException.ALREADY_EXIST_USERNAME_OR_EMAIL);
        verify(userRepository,times(1)).existsByUsernameAndEmail(anyString(), anyString());
        verify(userRepository,never()).save(any(User.class));
    }

    //=================Test_Change_Password=================
    @Test
    public void testChangePassword_WhenSuccess() {
        ChangePasswordReq req = ChangePasswordReq.builder()
                .oldPassword("123@L.quy5401")
                .newPassword("newPassword123@")
                .build();
        String token = "Bearer token";

        when(jwtUtil.extractUsername(token.substring(7))).thenReturn("votuan123");
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(req.getOldPassword(), user.getPassword())).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(user);

        authService.changePassword(req, token);

        verify(userRepository, times(1)).findByUsername(anyString());
        verify(passwordEncoder,times(1)).matches(anyString(),anyString());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testChangeForgot_WhenNotMatchPassword() {
        ChangePasswordReq changePasswordReq = ChangePasswordReq.builder()
                .oldPassword("123@L.quy5401")
                .newPassword("newPassword123@")
                .build();

        String token = "Bearer token";

        when(jwtUtil.extractUsername(token.substring(7))).thenReturn("votuan123");
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        lenient().when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThatThrownBy(() -> authService.changePassword(changePasswordReq,token))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining(MessageException.NOT_MATCH_PASSWORD);
        verify(jwtUtil,times(1)).extractUsername(anyString());
        verify(userRepository,times(1)).findByUsername(anyString());
        verify(passwordEncoder,times(1)).matches(anyString(),anyString());
        verify(userRepository,never()).save(any(User.class));
    }

    @Test
    public void testChangePassword_WhenNotFoundUser() {
        ChangePasswordReq changePasswordReq = ChangePasswordReq.builder().oldPassword("12345678").newPassword("123456723").build();
        String token = "Bearer token";

        when(jwtUtil.extractUsername(token.substring(7))).thenReturn("votuan123");
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.changePassword(changePasswordReq,token))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(MessageException.NOT_FOUND_USER);
        verify(jwtUtil,times(1)).extractUsername(anyString());
        verify(userRepository,times(1)).findByUsername(anyString());
        verify(passwordEncoder,never()).matches(anyString(),anyString());
        verify(userRepository,never()).save(any(User.class));
    }

    //==================Test_Forgot_Passwrod=================
    @Test
    public void testForgotPassword_WhenSuccess() {
        ForgotPasswordReq forgotPasswordReq = ForgotPasswordReq.builder()
                .email("votuan123@gmail.com")
                .oldPassword("123@L.quy5401")
                .newPassword("newPassword123@")
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(user);

        authService.forgotPassword(forgotPasswordReq);

        assertThat(user.getPassword()).isNotEqualTo("123@L.quy5401");
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(passwordEncoder,times(1)).matches(anyString(),anyString());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testForgotPassword_WhenUserNotFound() {
        ForgotPasswordReq forgotPasswordReq = ForgotPasswordReq.builder()
                .email("notfound@gmail.com")
                .oldPassword("12345678")
                .newPassword("newPassword")
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.forgotPassword(forgotPasswordReq))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(MessageException.NOT_FOUND_USER);
        verify(userRepository,times(1)).findByEmail(anyString());
        verify(passwordEncoder,never()).matches(anyString(),anyString());
        verify(userRepository,never()).save(any(User.class));
    }

    @Test
    public void testForgotPassword_WhenOldPasswordNotMatch() {
        ForgotPasswordReq forgotPasswordReq = ForgotPasswordReq.builder()
                .email("votuan123@gmail.com")
                .oldPassword("wrongPassword")
                .newPassword("newPassword")
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThatThrownBy(() -> authService.forgotPassword(forgotPasswordReq))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining(MessageException.NOT_MATCH_PASSWORD);
        verify(userRepository,times(1)).findByEmail(anyString());
        verify(passwordEncoder,times(1)).matches(anyString(),anyString());
        verify(userRepository,never()).save(any(User.class));
    }

    //=================Test_CreateVerify==================
    @Test
    public void testCreateVerification_WhenSuccess() {
        String email = "votuan123@gmail.com";

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(verifyEmailRepository.save(any(VerifyEmail.class))).thenReturn(verifyEmail);

        authService.createVerification(email);

        verify(userRepository, times(1)).findByEmail(anyString());
        verify(verifyEmailRepository, times(1)).save(any(VerifyEmail.class));
    }

    @Test
    public void testCreateVerification_WhenUserNotFound() {
        String email = "notfound@gmail.com";

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.createVerification(email))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(MessageException.NOT_FOUND_USER);
        verify(userRepository,times(1)).findByEmail(anyString());
        verify(verifyEmailRepository,never()).save(any(VerifyEmail.class));
    }

    //===================Test_Confirm_Verification======
    @Test
    public void testConfirmVerification_WhenSuccess() {
        String token = verifyEmail.getToken();
        String email = verifyEmail.getUser().getEmail();

        when(verifyEmailRepository.findByUserEmailAndToken(anyString(), anyString())).thenReturn(Optional.of(verifyEmail));
        authService.confirmVerification(token, email);

        verify(verifyEmailRepository, times(1)).findByUserEmailAndToken(anyString(), anyString());
    }

    @Test
    public void testConfirmVerification_WhenTokenVerifyNotFound() {
        String token = "invalidToken";
        String email = "votuan123@gmail.com";

        when(verifyEmailRepository.findByUserEmailAndToken(anyString(), anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.confirmVerification(token,email))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(MessageException.NOT_FOUND_TOKEN_VERIFY);
        verify(verifyEmailRepository,times(1)).findByUserEmailAndToken(anyString(), anyString());
    }

    @Test
    public void testConfirmVerification_WhenTokenExpired() {
        String token = verifyEmail.getToken();
        String email = verifyEmail.getUser().getEmail();
        verifyEmail.setExpiryDate(new Timestamp(System.currentTimeMillis() - 15*60*1000));

        when(verifyEmailRepository.findByUserEmailAndToken(anyString(), anyString())).thenReturn(Optional.of(verifyEmail));

        assertThatThrownBy(() -> authService.confirmVerification(token,email))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining(MessageException.TOKEN_EXPIRED);
        verify(verifyEmailRepository,times(1)).findByUserEmailAndToken(anyString(), anyString());
    }
}