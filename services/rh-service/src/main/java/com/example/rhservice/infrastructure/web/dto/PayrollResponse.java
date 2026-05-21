package com.example.rhservice.infrastructure.web.dto;

/** Response DTO for payroll data. */
public record PayrollResponse(
        Long id,
        String month,
        String year,
        Boolean paid,
        Double amount,
        Long employeeId
) {
}