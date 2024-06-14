package com.test.finalproject.service.impl;

import com.test.finalproject.config.JwtUtil;
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
                .orElseThrow(() -> new NotFoundException("404", "User Not Found"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new NotFoundException("404", "Wrong Password");
        }

        if (!user.getStatus().equals(AccountStatus.ACTIVE)) {
            throw new NotFoundException("404", "Account is locked");
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
            throw new NotFoundException("404","Email Already Exist");
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
    public void changePassword(ChangePasswordReq req, String token) {

        String username = jwtUtil.extractUsername(token.substring(7));

        final User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("404", "User Not Found"));


        if(!passwordEncoder.matches(req.getOldPassword(), user.getPassword())){
            throw new BadRequestException("400","Not Match Old Password");
        }

        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void createVerification(String email) {

        final User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("404", "User Not Found"));
        String token = UUID.randomUUID().toString();
        VerifyEmail verifyEmail = VerifyEmail.builder()
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
                .orElseThrow(() -> new NotFoundException("404", "Token Verify Not Found"));

        if(verifyEmail.isTokenExpired()){
            throw new BadRequestException("400","Token Expired");
        }
    }

    @Override
    public void forgotPassword(ForgotPasswordReq req) {
        final User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new NotFoundException("404","User not found"));

        if(!passwordEncoder.matches(req.getOldPassword(), user.getPassword())){
            throw new BadRequestException("400","Not Match Old Password");
        }

        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);
    }

}
