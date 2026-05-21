package com.example.rhservice.application.port.out;

import com.example.rhservice.domain.model.Employee;

/** Persistence port for employee-specific operations.
 *
 * Implementations (adapters) handle mapping and call the repository. Keep logic-free.
 */
public interface EmployeePersistencePort extends CrudPersistencePort<Employee, Long> {
}
