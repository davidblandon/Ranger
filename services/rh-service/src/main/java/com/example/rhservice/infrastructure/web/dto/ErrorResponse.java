package com.example.rhservice.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standardized error response for all API exceptions.
 * Provides clear, concise error information in English.
 */
public record ErrorResponse(
        @JsonProperty("timestamp")
        LocalDateTime timestamp,

        @JsonProperty("status")
        int status,

        @JsonProperty("error")
        String error,

        @JsonProperty("message")
        String message,

        @JsonProperty("path")
        String path,

        @JsonProperty("details")
        Map<String, Object> details
) {
    /**
     * Create an ErrorResponse with all fields.
     */
    public ErrorResponse(LocalDateTime timestamp, int status, String error, String message, String path, Map<String, Object> details) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.details = details;
    }

    /**
     * Create an ErrorResponse without details.
     */
    public ErrorResponse(LocalDateTime timestamp, int status, String error, String message, String path) {
        this(timestamp, status, error, message, path, null);
    }
}
