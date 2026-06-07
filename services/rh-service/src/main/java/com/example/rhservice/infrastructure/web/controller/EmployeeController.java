package com.example.rhservice.infrastructure.web.controller;

import com.example.rhservice.application.port.in.EmployeeService;
import com.example.rhservice.application.port.in.ShiftService;
import com.example.rhservice.domain.model.Employee;
import com.example.rhservice.domain.model.Payroll;
import com.example.rhservice.domain.model.Shift;
import com.example.rhservice.domain.model.UserRole;
import com.example.rhservice.infrastructure.web.dto.EmployeeRequest;
import com.example.rhservice.infrastructure.web.dto.EmployeeResponse;
import com.example.rhservice.infrastructure.web.security.CurrentUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
@Tag(name = "Employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final ShiftService shiftService;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserService currentUserService;

    public EmployeeController(EmployeeService employeeService, ShiftService shiftService, PasswordEncoder passwordEncoder, CurrentUserService currentUserService) {
        this.employeeService = employeeService;
        this.shiftService = shiftService;
        this.passwordEncoder = passwordEncoder;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public List<EmployeeResponse> findAll(Authentication authentication) {
        currentUserService.requireAdmin(authentication);
        return employeeService.findAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public EmployeeResponse findById(@PathVariable Long id, Authentication authentication) {
        currentUserService.requireSelfOrAdmin(authentication, id);
        return toResponse(loadEmployee(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeResponse create(@Valid @RequestBody EmployeeRequest request, Authentication authentication) {
        currentUserService.requireAdmin(authentication);
        Employee employee = new Employee();
        applyRequest(employee, request, false);
        return toResponse(employeeService.save(employee));
    }

    @PutMapping("/{id}")
    public EmployeeResponse update(@PathVariable Long id, @Valid @RequestBody EmployeeRequest request, Authentication authentication) {
        currentUserService.requireAdmin(authentication);
        Employee employee = loadEmployee(id);
        applyRequest(employee, request, true);
        return toResponse(employeeService.save(employee));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, Authentication authentication) {
        currentUserService.requireAdmin(authentication);
        employeeService.delete(id);
    }

    private Employee loadEmployee(Long id) {
        return employeeService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));
    }

    private void applyRequest(Employee employee, EmployeeRequest request, boolean preservePayrolls) {
        employee.setName(request.name());
        employee.setUsername(request.username());
        employee.setPassword(passwordEncoder.encode(request.password()));
        employee.setRole(UserRole.EMPLOYEE);
        employee.setTelephone(request.telephone());
        employee.setAddress(request.address());
        employee.setBankAccount(request.bankAccount());
        employee.setMonthlyHours(request.monthlyHours());
        employee.setSalary(request.salary());
        employee.setShift(resolveShift(request.shiftId()));
        if (!preservePayrolls) {
            employee.setPayrolls(new ArrayList<>());
        }
    }

    private Shift resolveShift(Long shiftId) {
        if (shiftId == null) {
            return null;
        }
        return shiftService.findById(shiftId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shift not found"));
    }

    private EmployeeResponse toResponse(Employee employee) {
        List<Long> payrollIds = employee.getPayrolls() == null
                ? List.of()
                : employee.getPayrolls().stream().map(Payroll::getId).toList();
        return new EmployeeResponse(
                employee.getId(),
                employee.getName(),
            employee.getUsername(),
            employee.getRole().name(),
                employee.getTelephone(),
                employee.getAddress(),
                employee.getBankAccount(),
                employee.getMonthlyHours(),
                employee.getSalary(),
                employee.getShift() == null ? null : employee.getShift().getId(),
                payrollIds
        );
    }
}