package com.example.eventplatform.repository;

import com.example.eventplatform.entity.User;
import com.example.eventplatform.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndRole(String email, UserRole role);
    Optional<User> findByEmailAndRoleNot(String email, UserRole role);
    boolean existsByEmail(String email);
}
