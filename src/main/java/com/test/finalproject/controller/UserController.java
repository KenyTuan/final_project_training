package com.test.finalproject.controller;

import com.test.finalproject.constants.ApiEndpoints;
import com.test.finalproject.model.dtos.user.UserRes;
import com.test.finalproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiEndpoints.PREFIX)
public class UserController {

    private final UserService userService;

    @GetMapping(ApiEndpoints.USER_V1)
    public List<UserRes> getAllUsers() {
        return userService.getAll();
    }

    @PatchMapping(ApiEndpoints.USER_V1 + "/{id}/locked")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void lockUser(@PathVariable int id) {
        userService.updateUserLock(id);
    }
}