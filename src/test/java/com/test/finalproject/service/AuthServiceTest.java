package com.test.finalproject.service;

import com.test.finalproject.config.JwtUtil;
import com.test.finalproject.entity.User;
import com.test.finalproject.enums.AccountStatus;
import com.test.finalproject.exception.BadRequestException;
import com.test.finalproject.exception.NotFoundException;
import com.test.finalproject.model.dtos.auth.*;
import com.test.finalproject.repository.UserRepository;
import com.test.finalproject.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .id(1)
                .username("votuan13")
                .email("votuan13@gmail.com")
                .password("123456")
                .status(AccountStatus.ACTIVE)
                .firstName("vo")
                .lastName("tuan")
                .build();
    }

    @Test
    public void testLogin_WhenSuccess() {
        AuthReq authReq = AuthReq.builder().username("votuan123").password("12345678").build();

        when(userRepository.findByUsername(authReq.getUsername())).thenReturn(Optional.of(user));

        when(jwtUtil.generateToken(user)).thenReturn("token");

        AuthRes authRes = authService.login(authReq);

        assertThat(authRes).isNotNull();
        assertThat(authRes.token()).isEqualTo("token");
    }

    @Test
    public void testLogin_WhenUserNotFound() {
        AuthReq authReq = AuthReq.builder().username("123ausscscs").password("12345678").build();

        when(userRepository.findByUsername(authReq.getUsername())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> authService.login(authReq));
    }

    @Test
    public void testRegister_WhenSuccess() {
        RegisterReq registerReq = RegisterReq.builder().username("votuan154").email("votuan154@gmail.com").password("12345678").firstName("vo").lastName("tuan").build();

        lenient().when(userRepository.existsByUsernameAndEmail(anyString(), anyString())).thenReturn(false);

        lenient().when(userRepository.save(any(User.class))).thenReturn(user);

        lenient().when(jwtUtil.generateToken(any(User.class))).thenReturn("token");

        AuthRes authRes = authService.register(registerReq);

        assertThat(authRes).isNotNull();

        assertThat(authRes.token()).isEqualTo("token");
    }

    @Test
    public void testRegister_WhenEmailExists() {
        RegisterReq registerReq = RegisterReq.builder().username("votuan13").email("votuan13@gmail.com").password("123456").firstName("vo").lastName("tuan").build();

        when(userRepository.existsByUsernameAndEmail(anyString(), anyString())).thenReturn(true);

        assertThrows(NotFoundException.class, () -> authService.register(registerReq));
    }

    @Test
    public void testForgotPassword_WhenSuccess() {
        ChangePasswordReq changePasswordReq = ChangePasswordReq.builder().oldPassword("12345678").newPassword("123456789").build();
        String token ="token";

        when(jwtUtil.extractUsername(token)).thenReturn("votuan13");
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        authService.changePassword(changePasswordReq, "token");

        verify(userRepository).save(user);
    }

    @Test
    public void testForgotPassword_WhenNotMatchPassword() {
        ChangePasswordReq changePasswordReq = ChangePasswordReq.builder().oldPassword("12345678").newPassword("123456723").build();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        lenient().when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(BadRequestException.class, () -> authService.changePassword(changePasswordReq, "token"));
    }

    @Test
    public void testForgotPassword_WhenNotFoundUser() {
        ChangePasswordReq changePasswordReq = ChangePasswordReq.builder().oldPassword("12345678").newPassword("123456723").build();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> authService.changePassword(changePasswordReq, "token"));
    }
}