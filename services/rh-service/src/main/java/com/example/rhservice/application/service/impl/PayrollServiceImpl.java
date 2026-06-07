package com.example.rhservice.application.service.impl;

import com.example.rhservice.application.port.in.PayrollService;
import com.example.rhservice.application.port.out.EmployeePersistencePort;
import com.example.rhservice.application.port.out.PayrollDocumentPort;
import com.example.rhservice.application.port.out.PayrollPersistencePort;
import com.example.rhservice.domain.model.Employee;
import com.example.rhservice.domain.model.Payroll;
import com.example.rhservice.domain.model.Shift;
import com.example.rhservice.domain.model.TimeBlock;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.List;

@Service
public class PayrollServiceImpl extends AbstractCrudService<Payroll, Long> implements PayrollService {

    private final EmployeePersistencePort employeePersistencePort;
    private final PayrollDocumentPort payrollDocumentPort;

    public PayrollServiceImpl(PayrollPersistencePort persistencePort,
                              EmployeePersistencePort employeePersistencePort,
                              PayrollDocumentPort payrollDocumentPort) {
        super(persistencePort);
        this.employeePersistencePort = employeePersistencePort;
        this.payrollDocumentPort = payrollDocumentPort;
    }

    @Override
    public Payroll generateMonthlyPayroll(Long employeeId, String month, String year) {
        Employee employee = employeePersistencePort.findById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));

        Payroll payroll = new Payroll();
        payroll.setMonth(month);
        payroll.setYear(year);
        payroll.setPaid(false);
        payroll.setEmployee(employee);
        payroll.setAmount(calculatePayrollAmount(employee));

        return save(payroll);
    }

    @Override
    public byte[] generatePayrollPaystub(Long payrollId) {
        Payroll payroll = findById(payrollId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payroll not found"));
        return payrollDocumentPort.generatePayrollPaystub(payroll);
    }

    private double calculatePayrollAmount(Employee employee) {
        double baseSalary = employee.getSalary();
        double expectedMonthlyHours = employee.getMonthlyHours();
        double actualMonthlyHours = calculateMonthlyWorkHours(employee.getShift());

        if (expectedMonthlyHours <= 0 || actualMonthlyHours <= expectedMonthlyHours) {
            return baseSalary;
        }

        double hourlyRate = baseSalary / expectedMonthlyHours;
        double overtimeHours = actualMonthlyHours - expectedMonthlyHours;
        double overtimeRate = hourlyRate * 1.1;
        double overtimePay = overtimeHours * overtimeRate;

        return Math.round((baseSalary + overtimePay) * 100.0) / 100.0;
    }

    private double calculateMonthlyWorkHours(Shift shift) {
        if (shift == null) {
            return 0;
        }

        double weeklyHours = totalHours(shift.getMonday())
                + totalHours(shift.getTuesday())
                + totalHours(shift.getWednesday())
                + totalHours(shift.getThursday())
                + totalHours(shift.getFriday())
                + totalHours(shift.getSaturday())
                + totalHours(shift.getSunday());

        return weeklyHours * 4;
    }

    private double totalHours(List<TimeBlock> blocks) {
        if (blocks == null) {
            return 0;
        }
        return blocks.stream().mapToDouble(this::durationHours).sum();
    }

    private double durationHours(TimeBlock block) {
        if (block == null || block.getStart() == null || block.getEnd() == null) {
            return 0;
        }
        return Math.max(0, Duration.between(block.getStart(), block.getEnd()).toMinutes() / 60.0);
    }
}