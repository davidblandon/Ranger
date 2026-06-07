package com.example.rhservice.application.port.out;

import com.example.rhservice.domain.model.Payroll;

/** Persistence port for Payroll entities. */
public interface PayrollPersistencePort extends CrudPersistencePort<Payroll, Long> {
}