package com.test.finalproject.service.impl;

import com.test.finalproject.constants.MessageException;
import com.test.finalproject.entity.User;
import com.test.finalproject.enums.AccountStatus;
import com.test.finalproject.exception.NotFoundException;
import com.test.finalproject.model.converter.UserDtoConverter;
import com.test.finalproject.model.dtos.user.UserRes;
import com.test.finalproject.repository.UserRepository;
import com.test.finalproject.service.MailService;
import com.test.finalproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final MailService mailService;

    @Override
    public List<UserRes> getAllUsers() {
        return UserDtoConverter.toModelList(userRepository.findAll()) ;
    }

    @Override
    @Transactional
    public UserRes updateUserLock(int id) {
        final User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(MessageException.NOT_FOUND_USER));

        user.setStatus(AccountStatus.LOCKED);
        userRepository.save(user);

        mailService.sendMail(user.getEmail(),"Account Locked!",
                "Hi, " + user.getFirstName() + " " + user.getFirstName() + "!\n Tài  khoản của bạn đã bị khóa vì bạn đã vi phạm bất thường.");
        return UserDtoConverter.toResponse(user);
    }

}
