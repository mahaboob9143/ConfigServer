package com.spring.ems.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.ems.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByContactNumber(String contactNumber);
    Optional<User> findByEmail(String email);
}
