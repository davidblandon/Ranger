package com.example.rhservice.infrastructure.web.dto;

import java.util.List;

/** Response DTO for employee data. */
public record EmployeeResponse(
        Long id,
        String name,
        String username,
        String role,
        String telephone,
        String address,
        String bankAccount,
        Double monthlyHours,
        Double salary,
        Long shiftId,
        List<Long> payrollIds
) {
}