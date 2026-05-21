package com.example.rhservice.infrastructure.web.dto;

/** Response DTO for admin data. */
public record AdminResponse(
        Long id,
        String name,
        String telephone,
        String address,
        String bankAccount,
        String permissions
) {
}