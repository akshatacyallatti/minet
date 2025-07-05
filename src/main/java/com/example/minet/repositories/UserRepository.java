package com.example.minet.repositories;

import com.example.minet.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    Optional<User> findById(Integer userId);


    Optional<User> findByEmailAndPassword(String email, String password);
}

