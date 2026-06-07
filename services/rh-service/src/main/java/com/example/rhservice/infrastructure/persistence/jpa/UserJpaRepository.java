package com.example.rhservice.infrastructure.persistence.jpa;

import com.example.rhservice.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/** JPA repository for the abstract User hierarchy. */
public interface UserJpaRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
}