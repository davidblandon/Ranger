package com.example.rhservice.infrastructure.web.controller;

import com.example.rhservice.application.port.in.EmployeeService;
import com.example.rhservice.application.port.in.PayrollService;
import com.example.rhservice.domain.model.Employee;
import com.example.rhservice.domain.model.Payroll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PayrollController.class)
class PayrollControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PayrollService payrollService;

    @MockBean
    private EmployeeService employeeService;

    @Test
    void shouldListPayrolls() throws Exception {
        Payroll payroll = buildPayroll(1L, 2L);
        when(payrollService.findAll()).thenReturn(List.of(payroll));

        mockMvc.perform(get("/api/payrolls"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].employeeId").value(2L));
    }

    @Test
    void shouldCreatePayroll() throws Exception {
        when(employeeService.findById(2L)).thenReturn(java.util.Optional.of(buildEmployee(2L)));
        when(payrollService.save(any(Payroll.class))).thenAnswer(invocation -> {
            Payroll payroll = invocation.getArgument(0);
            payroll.setId(3L);
            return payroll;
        });

        mockMvc.perform(post("/api/payrolls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"month":"May","year":"2026","paid":false,"amount":2500,"employeeId":2}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.employeeId").value(2L));
    }

    @Test
    void shouldGetPayrollById() throws Exception {
        when(payrollService.findById(1L)).thenReturn(java.util.Optional.of(buildPayroll(1L, 2L)));

        mockMvc.perform(get("/api/payrolls/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.month").value("May"));
    }

    @Test
    void shouldUpdatePayroll() throws Exception {
        when(payrollService.findById(1L)).thenReturn(java.util.Optional.of(buildPayroll(1L, 2L)));
        when(employeeService.findById(2L)).thenReturn(java.util.Optional.of(buildEmployee(2L)));
        when(payrollService.save(any(Payroll.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/api/payrolls/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"month":"June","year":"2026","paid":true,"amount":2600,"employeeId":2}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.month").value("June"))
                .andExpect(jsonPath("$.paid").value(true));
    }

    @Test
    void shouldDeletePayroll() throws Exception {
        mockMvc.perform(delete("/api/payrolls/1"))
                .andExpect(status().isNoContent());

        verify(payrollService).delete(1L);
    }

    private Payroll buildPayroll(Long id, Long employeeId) {
        Payroll payroll = new Payroll();
        payroll.setId(id);
        payroll.setMonth("May");
        payroll.setYear("2026");
        payroll.setPaid(false);
        payroll.setAmount(2500.0);
        payroll.setEmployee(buildEmployee(employeeId));
        return payroll;
    }

    private Employee buildEmployee(Long id) {
        Employee employee = new Employee();
        employee.setId(id);
        return employee;
    }
}