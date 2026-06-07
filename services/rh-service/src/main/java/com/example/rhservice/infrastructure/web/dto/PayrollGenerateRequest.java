package com.example.rhservice.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Request DTO for payroll generation based on employee shift schedule. */
public record PayrollGenerateRequest(
        @NotNull Long employeeId,
        @NotBlank String month,
        @NotBlank String year
) {
}
