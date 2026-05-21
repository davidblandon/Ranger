package com.example.rhservice.application.service.impl;

import com.example.rhservice.application.port.in.PayrollService;
import com.example.rhservice.application.port.out.PayrollPersistencePort;
import com.example.rhservice.domain.model.Payroll;
import org.springframework.stereotype.Service;

@Service
public class PayrollServiceImpl extends AbstractCrudService<Payroll, Long> implements PayrollService {

    /** Application service for Payroll use-cases. */
    public PayrollServiceImpl(PayrollPersistencePort persistencePort) {
        super(persistencePort);
    }
}