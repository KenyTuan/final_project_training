package com.test.finalproject.repository;

import com.test.finalproject.entity.PasswordRestToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerifyEmailRepository extends JpaRepository<PasswordRestToken, Integer> {

    boolean existsByUserEmailAndToken(String email, String token);
    Optional<PasswordRestToken> findByUserEmailAndToken(String email, String token);
}
