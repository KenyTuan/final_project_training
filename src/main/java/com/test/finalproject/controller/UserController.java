package com.test.finalproject.controller;

import com.test.finalproject.constants.ApiEndpoints;
import com.test.finalproject.enums.RoleName;
import com.test.finalproject.model.dtos.user.UserRes;
import com.test.finalproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiEndpoints.PREFIX)
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    @GetMapping(ApiEndpoints.USER_V1)
    public List<UserRes> getAllUsers() {
        return userService.getAllUsers();
    }

    @PatchMapping(ApiEndpoints.USER_V1 + "/{id}/lock")
    public UserRes lockUser(@PathVariable int id) {
        return userService.updateUserLock(id);
    }
}
