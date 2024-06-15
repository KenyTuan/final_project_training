package com.test.finalproject.service.impl;

import com.test.finalproject.config.JwtUtil;
import com.test.finalproject.constants.MessageException;
import com.test.finalproject.entity.User;
import com.test.finalproject.entity.VerifyEmail;
import com.test.finalproject.enums.AccountStatus;
import com.test.finalproject.exception.BadRequestException;
import com.test.finalproject.exception.NotFoundException;
import com.test.finalproject.model.converter.UserDtoConverter;
import com.test.finalproject.model.dtos.auth.*;
import com.test.finalproject.repository.UserRepository;
import com.test.finalproject.repository.VerifyEmailRepository;
import com.test.finalproject.service.AuthService;
import com.test.finalproject.service.MailService;
import lombok.RequiredArgsConstructor;
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
    private static final int EXPIRATION = 60 * 15 *1000;

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;

    private final AuthenticationManager authenticationManager;
    private final VerifyEmailRepository verifyEmailRepository;
    private final MailService mailService;

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
        final boolean existEmail = userRepository.existsByUsernameAndEmail(req.getUsername(), req.getEmail());

        if (existEmail) {
            throw new NotFoundException(MessageException.ALREADY_EXIST_USERNAME_OR_EMAIL);
        }

        final User user = UserDtoConverter.toEntity(req);

        user.setStatus(AccountStatus.ACTIVE);
        user.setPassword(passwordEncoder.encode(req.getPassword()));

        userRepository.save(user);


        return new AuthRes(
                jwtUtil.generateToken(user)
        );
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordReq req, String token) {

        String username = jwtUtil.extractUsername(token.substring(7));

        final User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(MessageException.NOT_FOUND_USER));


        if(!passwordEncoder.matches(req.getOldPassword(), user.getPassword())){
            throw new BadRequestException(MessageException.NOT_MATCH_PASSWORD);
        }

        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void createVerification(String email) {

        final User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(MessageException.NOT_FOUND_USER));
        final String token = UUID.randomUUID().toString();
        final VerifyEmail verifyEmail = VerifyEmail.builder()
                .token(token)
                .expiryDate(new Timestamp(System.currentTimeMillis() + EXPIRATION))
                .user(user).build();

        verifyEmailRepository.save(verifyEmail);

        mailService.sendMail(user.getEmail(),"Verify Change Password!",
                "Chào, " + user.getFirstName() + " " + user.getLastName() + "!\n Đây là mã xác thực: " + token);
    }

    @Override
    public void confirmVerification(String token, String email) {

        final VerifyEmail verifyEmail = verifyEmailRepository.findByUserEmailAndToken(email,token)
                .orElseThrow(() -> new NotFoundException(MessageException.NOT_FOUND_TOKEN_VERIFY));

        if(verifyEmail.isTokenExpired()){
            throw new BadRequestException(MessageException.TOKEN_EXPIRED);
        }
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordReq req) {
        final User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new NotFoundException(MessageException.NOT_FOUND_USER));

        if(!passwordEncoder.matches(req.getOldPassword(), user.getPassword())){
            throw new BadRequestException(MessageException.NOT_MATCH_PASSWORD);
        }

        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);
    }

}
