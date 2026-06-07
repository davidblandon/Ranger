package com.example.rhservice.infrastructure.persistence.adapter;

import com.example.rhservice.application.port.out.UserLookupPort;
import com.example.rhservice.domain.model.User;
import com.example.rhservice.infrastructure.persistence.jpa.UserJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/** Adapter that exposes user lookup through the persistence port. */
@Repository
public class UserLookupAdapter implements UserLookupPort {

    private final UserJpaRepository repository;

    public UserLookupAdapter(UserJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return repository.findByUsername(username);
    }
}