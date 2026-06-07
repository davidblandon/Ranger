package com.example.rhservice.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/** Request DTO for creating or updating an employee. */
public record EmployeeRequest(
        @NotBlank String name,
        @NotBlank String username,
        @NotBlank String password,
        @NotBlank String telephone,
        @NotBlank String address,
        @NotBlank String bankAccount,
        @NotNull @PositiveOrZero Double monthlyHours,
        @NotNull @PositiveOrZero Double salary,
        Long shiftId
) {
}