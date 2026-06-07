package com.example.rhservice.infrastructure.web.dto;

import com.example.rhservice.domain.model.UserRole;

/** Login response payload. */
public record AuthResponse(
        Long id,
        String username,
        UserRole role
) {
}