package com.example.rhservice.infrastructure.web.dto;

import java.time.LocalTime;

/** Response DTO for a time interval in a shift schedule. */
public record TimeBlockResponse(
        LocalTime start,
        LocalTime end
) {
}