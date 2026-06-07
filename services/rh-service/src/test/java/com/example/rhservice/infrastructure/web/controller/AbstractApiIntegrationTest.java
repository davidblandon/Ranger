package com.example.rhservice.infrastructure.web.controller;

import com.example.rhservice.domain.model.Admin;
import com.example.rhservice.domain.model.Employee;
import com.example.rhservice.domain.model.Payroll;
import com.example.rhservice.domain.model.Shift;
import com.example.rhservice.domain.model.TimeBlock;
import com.example.rhservice.infrastructure.persistence.jpa.AdminJpaRepository;
import com.example.rhservice.infrastructure.persistence.jpa.EmployeeJpaRepository;
import com.example.rhservice.infrastructure.persistence.jpa.PayrollJpaRepository;
import com.example.rhservice.infrastructure.persistence.jpa.ShiftJpaRepository;
import com.example.rhservice.infrastructure.persistence.jpa.UserJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

abstract class AbstractApiIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected UserJpaRepository userJpaRepository;

    @Autowired
    protected AdminJpaRepository adminJpaRepository;

    @Autowired
    protected EmployeeJpaRepository employeeJpaRepository;

    @Autowired
    protected PayrollJpaRepository payrollJpaRepository;

    @Autowired
    protected ShiftJpaRepository shiftJpaRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    protected MockHttpSession adminSession;
    protected MockHttpSession employeeSession;

    protected Long adminId;
    protected Long employeeId;
    protected Long employeeShiftId;
    protected Long employeePayrollId;
    protected Long otherEmployeeId;
    protected Long otherShiftId;
    protected Long otherPayrollId;
    protected Long otherAdminId;

    @BeforeEach
    void setUpApiData() throws Exception {
        seedCommonData();
        adminSession = login("admin", "supersecurepassword");
        employeeSession = login("employee1", "supernormalpassword");
    }

    protected MockHttpSession login(String username, String password) throws Exception {
        return (MockHttpSession) mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"%s","password":"%s"}
                                """.formatted(username, password)))
                .andExpect(status().isOk())
                .andReturn()
                .getRequest()
                .getSession(false);
    }

    private void seedCommonData() {
        Admin admin = (Admin) userJpaRepository.findByUsername("admin")
                .orElseThrow(() -> new IllegalStateException("Default admin was not seeded"));
        adminId = admin.getId();

        Shift employeeShift = new Shift();
        employeeShift.setMonday(List.of(new TimeBlock(LocalTime.of(8, 0), LocalTime.of(12, 0))));
        employeeShift.setTuesday(List.of());
        employeeShift.setWednesday(List.of());
        employeeShift.setThursday(List.of());
        employeeShift.setFriday(List.of());
        employeeShift.setSaturday(List.of());
        employeeShift.setSunday(List.of());
        employeeShift = shiftJpaRepository.save(employeeShift);
        employeeShiftId = employeeShift.getId();

        Employee employee = (Employee) userJpaRepository.findByUsername("employee1")
                .orElseThrow(() -> new IllegalStateException("Default employee was not seeded"));
        employee.setShift(employeeShift);
        employee = employeeJpaRepository.save(employee);
        employeeId = employee.getId();

        Payroll payroll = new Payroll();
        payroll.setMonth("June");
        payroll.setYear("2026");
        payroll.setPaid(false);
        payroll.setAmount(2500.0);
        payroll.setEmployee(employee);
        payroll = payrollJpaRepository.save(payroll);
        employeePayrollId = payroll.getId();

        employee.getPayrolls().add(payroll);
        employeeJpaRepository.save(employee);

        Shift otherShift = new Shift();
        otherShift.setMonday(List.of(new TimeBlock(LocalTime.of(13, 0), LocalTime.of(17, 0))));
        otherShift.setTuesday(List.of());
        otherShift.setWednesday(List.of());
        otherShift.setThursday(List.of());
        otherShift.setFriday(List.of());
        otherShift.setSaturday(List.of());
        otherShift.setSunday(List.of());
        otherShift = shiftJpaRepository.save(otherShift);
        otherShiftId = otherShift.getId();

        Employee otherEmployee = new Employee();
        otherEmployee.setName("Employee Two");
        otherEmployee.setUsername("employee2");
        otherEmployee.setPassword(passwordEncoder.encode("employee2password"));
        otherEmployee.setRole(com.example.rhservice.domain.model.UserRole.EMPLOYEE);
        otherEmployee.setTelephone("555-200-200");
        otherEmployee.setAddress("Second Street 2");
        otherEmployee.setBankAccount("ES000000002");
        otherEmployee.setMonthlyHours(160.0);
        otherEmployee.setSalary(2200.0);
        otherEmployee.setShift(otherShift);
        otherEmployee = employeeJpaRepository.save(otherEmployee);
        otherEmployeeId = otherEmployee.getId();

        Payroll otherPayroll = new Payroll();
        otherPayroll.setMonth("June");
        otherPayroll.setYear("2026");
        otherPayroll.setPaid(true);
        otherPayroll.setAmount(2200.0);
        otherPayroll.setEmployee(otherEmployee);
        otherPayroll = payrollJpaRepository.save(otherPayroll);
        otherPayrollId = otherPayroll.getId();

        otherEmployee.getPayrolls().add(otherPayroll);
        employeeJpaRepository.save(otherEmployee);

        Admin otherAdmin = new Admin();
        otherAdmin.setName("Admin Two");
        otherAdmin.setUsername("admin2");
        otherAdmin.setPassword(passwordEncoder.encode("admin2password"));
        otherAdmin.setRole(com.example.rhservice.domain.model.UserRole.ADMIN);
        otherAdmin.setTelephone("555-300-300");
        otherAdmin.setAddress("Admin Avenue 2");
        otherAdmin.setBankAccount("ES000000003");
        otherAdmin.setPermissions("ROLE_HR_MANAGER");
        otherAdmin = adminJpaRepository.save(otherAdmin);
        otherAdminId = otherAdmin.getId();
    }
}