package com.example.rhservice.application.port.in;

import com.example.rhservice.domain.model.UserRole;

/** Authenticated user data returned by the login use case. */
public record AuthSession(Long id, String username, UserRole role) {
}