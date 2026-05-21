package com.example.rhservice.infrastructure.web.dto;

import java.util.List;

/** Response DTO for a weekly shift schedule. */
public record ShiftResponse(
        Long id,
        List<TimeBlockResponse> monday,
        List<TimeBlockResponse> tuesday,
        List<TimeBlockResponse> wednesday,
        List<TimeBlockResponse> thursday,
        List<TimeBlockResponse> friday,
        List<TimeBlockResponse> saturday,
        List<TimeBlockResponse> sunday
) {
}