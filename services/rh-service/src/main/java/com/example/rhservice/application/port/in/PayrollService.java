package com.example.rhservice.application.port.in;

import com.example.rhservice.domain.model.Payroll;

/** Use-case interface for payroll operations (CRUD). */
public interface PayrollService extends CrudUseCase<Payroll, Long> {

    Payroll generateMonthlyPayroll(Long employeeId, String month, String year);

    byte[] generatePayrollPaystub(Long payrollId);
}