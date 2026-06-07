package com.example.rhservice.application.port.out;

import com.example.rhservice.domain.model.Payroll;

public interface PayrollDocumentPort {

    byte[] generatePayrollPaystub(Payroll payroll);
}
