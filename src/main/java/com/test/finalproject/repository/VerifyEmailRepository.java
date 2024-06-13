package com.test.finalproject.repository;

import com.test.finalproject.entity.VerifyEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerifyEmailRepository extends JpaRepository<VerifyEmail, Integer> {

    boolean existsByUserEmailAndToken(String email, String token);
    Optional<VerifyEmail> findByUserEmailAndToken(String email, String token);
}
