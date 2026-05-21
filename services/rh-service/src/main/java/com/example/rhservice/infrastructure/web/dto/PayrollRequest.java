package com.example.rhservice.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/** Request DTO for creating or updating a payroll. */
public record PayrollRequest(
        @NotBlank String month,
        @NotBlank String year,
        @NotNull Boolean paid,
        @NotNull @PositiveOrZero Double amount,
        @NotNull Long employeeId
) {
}