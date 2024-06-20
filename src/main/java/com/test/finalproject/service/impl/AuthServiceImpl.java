package com.test.finalproject.service.impl;

import com.test.finalproject.config.JwtUtil;
import com.test.finalproject.constants.MessageException;
import com.test.finalproject.entity.User;
import com.test.finalproject.enums.AccountStatus;
import com.test.finalproject.enums.RoleName;
import com.test.finalproject.exception.BadRequestException;
import com.test.finalproject.exception.NotFoundException;
import com.test.finalproject.model.converter.UserDtoConverter;
import com.test.finalproject.model.dtos.auth.*;
import com.test.finalproject.repository.UserRepository;
import com.test.finalproject.service.AuthService;
import com.test.finalproject.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Value("${application.mail.expiration}")
    private int EXPIRATION;

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;

    private final AuthenticationManager authenticationManager;
    private final MailService mailService;
    private final JavaMailSender mailSender;

    @Override
    public AuthRes login(AuthReq req) {

        final User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new NotFoundException(MessageException.NOT_FOUND_USER));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new BadRequestException(MessageException.NOT_MATCH_PASSWORD);
        }

        if (!user.getStatus().equals(AccountStatus.ACTIVE)) {
            throw new BadRequestException(MessageException.ACCOUNT_LOCKED);
        }

        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));

        return new AuthRes(jwtUtil.generateToken(user));
    }

    @Override
    @Transactional
    public AuthRes register(RegisterReq req) {
        final boolean existEmail = userRepository.existsByUsernameOrEmail(req.getUsername(), req.getEmail());

        if (existEmail) {
            throw new NotFoundException(MessageException.ALREADY_EXIST_USERNAME_OR_EMAIL);
        }

        final User user = UserDtoConverter.toEntity(req);

        user.setStatus(AccountStatus.ACTIVE);
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(RoleName.USER);
        userRepository.save(user);


        return new AuthRes(
                jwtUtil.generateToken(user)
        );
    }

    @Override
    @Transactional
    public void requestForgotPassword(String email) {

        final User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(MessageException.NOT_FOUND_USER));

        final String token = UUID.randomUUID().toString();

        user.setToken(token);
        user.setExpiryDate(new Timestamp(System.currentTimeMillis() + EXPIRATION));

        userRepository.save(user);

        mailService.sendMail(user.getEmail(),"Verification Code!",
                "Hi, " + user.getFirstName() + "!\n\nYour verification code: " + token);
    }

    @Override
    @Transactional
    public void forgotPassword(RequestForgot requestForgot) {

        final User user = userRepository.findByEmailAndToken(requestForgot.getEmail(),requestForgot.getToken())
                .orElseThrow(() -> new NotFoundException(MessageException.NOT_FOUND_TOKEN_VERIFY));

        if(user.isTokenExpired()){
            throw new BadRequestException(MessageException.TOKEN_EXPIRED);
        }

        user.setExpiryDate(new Timestamp(System.currentTimeMillis()));
        user.setPassword(passwordEncoder.encode(requestForgot.getPassword()));

        userRepository.save(user);

        mailService.sendMail(user.getEmail(),"Complete Reset Password!",
                "Hi, " + user.getFirstName() + "!\n\nCompleted Reset Password!");
    }

}
