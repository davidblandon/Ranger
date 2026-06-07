package com.example.rhservice.infrastructure.web.controller;

import com.example.rhservice.application.port.in.EmployeeService;
import com.example.rhservice.application.port.in.PayrollService;
import com.example.rhservice.domain.model.Employee;
import com.example.rhservice.domain.model.Payroll;
import com.example.rhservice.infrastructure.web.dto.PayrollGenerateRequest;
import com.example.rhservice.infrastructure.web.dto.PayrollRequest;
import com.example.rhservice.infrastructure.web.dto.PayrollResponse;
import com.example.rhservice.infrastructure.web.security.CurrentUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    private final CurrentUserService currentUserService;

    public PayrollController(PayrollService payrollService, EmployeeService employeeService, CurrentUserService currentUserService) {
        this.payrollService = payrollService;
        this.employeeService = employeeService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public List<PayrollResponse> findAll(Authentication authentication) {
        return currentUserService.filterPayrolls(authentication, payrollService.findAll()).stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public PayrollResponse findById(@PathVariable Long id, Authentication authentication) {
        Payroll payroll = loadPayroll(id);
        currentUserService.requirePayrollAccess(authentication, payroll);
        return toResponse(payroll);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PayrollResponse create(@Valid @RequestBody PayrollRequest request) {
        Payroll payroll = new Payroll();
        applyRequest(payroll, request);
        return toResponse(payrollService.save(payroll));
    }

    @PostMapping("/generate")
    @ResponseStatus(HttpStatus.CREATED)
    public PayrollResponse generate(@Valid @RequestBody PayrollGenerateRequest request) {
        return toResponse(payrollService.generateMonthlyPayroll(request.employeeId(), request.month(), request.year()));
    }

    @GetMapping(value = "/{id}/paystub", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> downloadPaystub(@PathVariable Long id, Authentication authentication) {
        Payroll payroll = loadPayroll(id);
        currentUserService.requirePayrollAccess(authentication, payroll);
        byte[] pdf = payrollService.generatePayrollPaystub(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=paystub-%d.pdf".formatted(id))
                .body(pdf);
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