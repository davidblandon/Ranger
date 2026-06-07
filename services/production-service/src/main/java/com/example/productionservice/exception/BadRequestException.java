package com.example.productionservice.exception;

/** Thrown when a request violates a business rule. Maps to HTTP 400. */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
