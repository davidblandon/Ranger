package com.example.rhservice.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;

/** Login request payload. */
public record AuthRequest(
        @NotBlank String username,
        @NotBlank String password
) {
}