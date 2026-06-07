package com.example.rhservice.application.port.out;

import com.example.rhservice.domain.model.User;

import java.util.Optional;

/** Read-only lookup for application users. */
public interface UserLookupPort {

    Optional<User> findByUsername(String username);
}