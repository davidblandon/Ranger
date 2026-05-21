package com.example.rhservice.application.service.impl;

import com.example.rhservice.application.port.in.EmployeeService;
import com.example.rhservice.application.port.out.EmployeePersistencePort;
import com.example.rhservice.domain.model.Employee;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends AbstractCrudService<Employee, Long> implements EmployeeService {

    /**
     * Application service for Employee use-cases.
     *
     * <p>Implements the input port `EmployeeService` and delegates all CRUD operations to the
     * {@link com.example.rhservice.application.port.out.EmployeePersistencePort} via the
     * {@link AbstractCrudService} base class. Constructor injection enforces dependencies and improves testability.
     */
    public EmployeeServiceImpl(EmployeePersistencePort persistencePort) {
        super(persistencePort);
    }
}