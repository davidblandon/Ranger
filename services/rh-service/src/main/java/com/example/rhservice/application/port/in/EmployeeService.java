package com.example.rhservice.application.port.in;

import com.example.rhservice.domain.model.Employee;

/** Application-level use case interface for employee operations.
 *
 * This is an input port (driven port) that controllers or other entry-points call. It extends the generic
 * {@link com.example.rhservice.application.port.in.CrudUseCase} to provide standard CRUD operations for Employee.
 */
public interface EmployeeService extends CrudUseCase<Employee, Long> {
}