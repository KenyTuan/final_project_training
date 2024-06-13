package com.test.finalproject.service;

import com.test.finalproject.entity.User;
import com.test.finalproject.enums.AccountStatus;
import com.test.finalproject.exception.NotFoundException;
import com.test.finalproject.repository.UserRepository;
import com.test.finalproject.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    public void setUp() {
        user = User
                .builder()
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
    public void testUpdateUserLock_WhenSuccess() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        assertThat(userRepository.findById(1)).isNotNull();
    }

    @Test
    public void testGetAllUsers_ReturnsUserList() {
        var userList = new ArrayList<User>();
        userList.add(user);

        when(userRepository.findAll()).thenReturn(userList);

        var result = userService.getAll();

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertEquals(result.get(0).email(),user.getEmail());
        assertEquals(result.get(0).firstName(),user.getFirstName());
        assertEquals(result.get(0).lastName(),user.getLastName());

    }

    @Test
    public void testUpdateUserLock_WhenUserNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUserLock(2));

        assertThat(userRepository.findById(2)).isEmpty();
    }

    @Test
    public void testGetAllUsers_ReturnsEmptyList() {
        when(userRepository.findAll()).thenReturn(new ArrayList<>());

        var userList = userService.getAll();

        assertThat(userList).isNotNull();
        assertThat(userList.size()).isEqualTo(0);
    }




}
