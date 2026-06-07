package com.example.productionservice.exception;

import java.time.Instant;
import java.util.Map;

/** Consistent error response body returned by {@link GlobalExceptionHandler}. */
public record ApiError(
    Instant timestamp,
    int status,
    String error,
    String message,
    Map<String, String> fieldErrors
) {
    public static ApiError of(int status, String error, String message) {
        return new ApiError(Instant.now(), status, error, message, null);
    }

    public static ApiError of(int status, String error, String message, Map<String, String> fieldErrors) {
        return new ApiError(Instant.now(), status, error, message, fieldErrors);
    }
}
