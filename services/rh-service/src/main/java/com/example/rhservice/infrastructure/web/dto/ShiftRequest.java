package com.example.rhservice.infrastructure.web.dto;

import jakarta.validation.Valid;

import java.util.List;

/** Request DTO for a weekly shift schedule. */
public record ShiftRequest(
        @Valid List<TimeBlockRequest> monday,
        @Valid List<TimeBlockRequest> tuesday,
        @Valid List<TimeBlockRequest> wednesday,
        @Valid List<TimeBlockRequest> thursday,
        @Valid List<TimeBlockRequest> friday,
        @Valid List<TimeBlockRequest> saturday,
        @Valid List<TimeBlockRequest> sunday
) {
}