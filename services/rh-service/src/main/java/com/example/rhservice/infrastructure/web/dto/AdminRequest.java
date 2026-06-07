package com.example.rhservice.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;

/** Request DTO for creating or updating an admin. */
public record AdminRequest(
        @NotBlank String name,
        @NotBlank String username,
        @NotBlank String password,
        @NotBlank String telephone,
        @NotBlank String address,
        @NotBlank String bankAccount,
        @NotBlank String permissions
) {
}