package com.example.rhservice.infrastructure.web.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

/** Request DTO for a time interval in a shift schedule. */
public record TimeBlockRequest(
        @NotNull LocalTime start,
        @NotNull LocalTime end
) {
}