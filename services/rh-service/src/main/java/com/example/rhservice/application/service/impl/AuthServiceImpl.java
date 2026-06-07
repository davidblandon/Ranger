package com.example.rhservice.application.service.impl;

import com.example.rhservice.application.port.in.AuthService;
import com.example.rhservice.application.port.in.AuthSession;
import com.example.rhservice.application.port.out.UserLookupPort;
import com.example.rhservice.domain.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserLookupPort userLookupPort;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserLookupPort userLookupPort, PasswordEncoder passwordEncoder) {
        this.userLookupPort = userLookupPort;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthSession login(String username, String password) {
        User user = userLookupPort.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        return new AuthSession(user.getId(), user.getUsername(), user.getRole());
    }
}