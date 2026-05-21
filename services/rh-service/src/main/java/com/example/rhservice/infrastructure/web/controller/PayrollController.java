package com.example.rhservice.infrastructure.web.controller;

import com.example.rhservice.application.port.in.EmployeeService;
import com.example.rhservice.application.port.in.PayrollService;
import com.example.rhservice.domain.model.Employee;
import com.example.rhservice.domain.model.Payroll;
import com.example.rhservice.infrastructure.web.dto.PayrollRequest;
import com.example.rhservice.infrastructure.web.dto.PayrollResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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

import java.util.List;

@RestController
@RequestMapping("/api/payrolls")
@Tag(name = "Payrolls")
public class PayrollController {

    private final PayrollService payrollService;
    private final EmployeeService employeeService;

    public PayrollController(PayrollService payrollService, EmployeeService employeeService) {
        this.payrollService = payrollService;
        this.employeeService = employeeService;
    }

    @GetMapping
    public List<PayrollResponse> findAll() {
        return payrollService.findAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public PayrollResponse findById(@PathVariable Long id) {
        return toResponse(loadPayroll(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PayrollResponse create(@Valid @RequestBody PayrollRequest request) {
        Payroll payroll = new Payroll();
        applyRequest(payroll, request);
        return toResponse(payrollService.save(payroll));
    }

    @PutMapping("/{id}")
    public PayrollResponse update(@PathVariable Long id, @Valid @RequestBody PayrollRequest request) {
        Payroll payroll = loadPayroll(id);
        applyRequest(payroll, request);
        return toResponse(payrollService.save(payroll));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        payrollService.delete(id);
    }

    private Payroll loadPayroll(Long id) {
        return payrollService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payroll not found"));
    }

    private void applyRequest(Payroll payroll, PayrollRequest request) {
        payroll.setMonth(request.month());
        payroll.setYear(request.year());
        payroll.setPaid(Boolean.TRUE.equals(request.paid()));
        payroll.setAmount(request.amount());
        payroll.setEmployee(resolveEmployee(request.employeeId()));
    }

    private Employee resolveEmployee(Long employeeId) {
        return employeeService.findById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));
    }

    private PayrollResponse toResponse(Payroll payroll) {
        return new PayrollResponse(
                payroll.getId(),
                payroll.getMonth(),
                payroll.getYear(),
                payroll.isPaid(),
                payroll.getAmount(),
                payroll.getEmployee() == null ? null : payroll.getEmployee().getId()
        );
    }
}