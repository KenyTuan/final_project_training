package com.test.finalproject.service.impl;

import com.test.finalproject.config.JwtUtil;
import com.test.finalproject.constants.MessageException;
import com.test.finalproject.entity.User;
import com.test.finalproject.entity.PasswordRestToken;
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
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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

        userRepository.save(user);


        return new AuthRes(
                jwtUtil.generateToken(user)
        );
    }

    @Override
    @Transactional
    public void createPasswordRestToken(String email) {

        final User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(MessageException.NOT_FOUND_USER));
        final String token = UUID.randomUUID().toString();
        final PasswordRestToken passwordRestToken = PasswordRestToken.builder()
                .token(token)
                .expiryDate(new Timestamp(System.currentTimeMillis() + EXPIRATION))
                .user(user).build();

        verifyEmailRepository.save(passwordRestToken);

        mailService.sendMail(user.getEmail(),"Verify Change Password!",
                "Chào, " + user.getFirstName() + " " + user.getLastName() + "!\n Đây là mã xác thực: " + token);
    }

    @Override
    @Transactional
    public void confirmPasswordRestToken(String token, String email) {

        final PasswordRestToken passwordRestToken = verifyEmailRepository.findByUserEmailAndToken(email,token)
                .orElseThrow(() -> new NotFoundException(MessageException.NOT_FOUND_TOKEN_VERIFY));

        if(passwordRestToken.isTokenExpired()){
            throw new BadRequestException(MessageException.TOKEN_EXPIRED);
        }

        verifyEmailRepository.delete(passwordRestToken);

        final User user = passwordRestToken.getUser();

        String password = generateSecureRandomPassword();

        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);

        mailService.sendMail(user.getEmail(),"Your Password Rest!",
                "Chào, " + user.getFirstName() + " " + user.getLastName() + "!\n Your password : " + password);
    }

    public String generateSecureRandomPassword() {
        Stream<Character> pwdStream = Stream.concat(getRandomNumbers(2),
                Stream.concat(getRandomSpecialChars(2),
                        Stream.concat(getRandomAlphabets(2, true), getRandomAlphabets(4, false))));
        List<Character> charList = pwdStream.collect(Collectors.toList());
        Collections.shuffle(charList);
        return charList.stream()
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

    private Stream<Character> getRandomSpecialChars(int count) {
        Random random = new SecureRandom();
        IntStream specialChars = random.ints(count, 33, 45);
        return specialChars.mapToObj(data -> (char) data);
    }

    private Stream<Character> getRandomNumbers(int count) {
        Random random = new SecureRandom();
        IntStream specialChars = random.ints(count, 48, 57);
        return specialChars.mapToObj(data -> (char) data);
    }

    private Stream<Character> getRandomAlphabets(int count, boolean checkToUpper) {
        Random random = new SecureRandom();

        int characterFrom = checkToUpper ? 41 : 61;
        int characterTo = checkToUpper ? 90 : 122;

        IntStream specialChars = random.ints(count, characterFrom, characterTo);
        return specialChars.mapToObj(data -> (char) data);
    }


}
