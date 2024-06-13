package com.test.finalproject.repository;

import com.test.finalproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByUsernameAndEmail(String username, String email);

    Optional<User> findByUsername(String username);

}
