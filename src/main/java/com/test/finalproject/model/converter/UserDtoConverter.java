package com.test.finalproject.model.converter;

import com.test.finalproject.entity.User;
import com.test.finalproject.model.dtos.auth.RegisterReq;
import com.test.finalproject.model.dtos.user.UserRes;

import java.util.Collections;
import java.util.List;

public class UserDtoConverter {

    public static List<UserRes> toModelList(List<User> listEntity) {
        return listEntity.stream()
                .map(UserDtoConverter::toResponse)
                .toList();
    }

    private static UserRes toResponse(User user) {
        return new UserRes(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getTasks() != null ? TaskDtoConverter.toModelList(user.getTasks()): Collections.emptyList()
        );
    }

    public static User toEntity(RegisterReq req) {
        return User.builder()
                .username(req.getUsername())
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .email(req.getEmail())
                .build();
    }
}
