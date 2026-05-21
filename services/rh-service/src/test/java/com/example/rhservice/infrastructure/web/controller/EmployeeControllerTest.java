package com.example.rhservice.infrastructure.web.controller;

import com.example.rhservice.application.port.in.EmployeeService;
import com.example.rhservice.application.port.in.ShiftService;
import com.example.rhservice.domain.model.Employee;
import com.example.rhservice.domain.model.Shift;
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

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @MockBean
    private ShiftService shiftService;

    @Test
    void shouldListEmployees() throws Exception {
        Employee employee = buildEmployee(1L, 10L);
        when(employeeService.findAll()).thenReturn(List.of(employee));

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].shiftId").value(10L));
    }

    @Test
    void shouldCreateEmployee() throws Exception {
        Shift shift = buildShift(10L);
        when(shiftService.findById(10L)).thenReturn(java.util.Optional.of(shift));
        when(employeeService.save(any(Employee.class))).thenAnswer(invocation -> {
            Employee employee = invocation.getArgument(0);
            employee.setId(2L);
            return employee;
        });

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Ana","telephone":"555","address":"Street","bankAccount":"IBAN","monthlyHours":160,"salary":2500,"shiftId":10}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.shiftId").value(10L));
    }

    @Test
    void shouldGetEmployeeById() throws Exception {
        when(employeeService.findById(1L)).thenReturn(java.util.Optional.of(buildEmployee(1L, 10L)));

        mockMvc.perform(get("/api/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ana"));
    }

    @Test
    void shouldUpdateEmployee() throws Exception {
        Shift shift = buildShift(10L);
        when(employeeService.findById(1L)).thenReturn(java.util.Optional.of(buildEmployee(1L, 10L)));
        when(shiftService.findById(10L)).thenReturn(java.util.Optional.of(shift));
        when(employeeService.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/api/employees/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Ana 2","telephone":"555","address":"Street","bankAccount":"IBAN","monthlyHours":180,"salary":2600,"shiftId":10}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ana 2"))
                .andExpect(jsonPath("$.monthlyHours").value(180.0));
    }

    @Test
    void shouldDeleteEmployee() throws Exception {
        mockMvc.perform(delete("/api/employees/1"))
                .andExpect(status().isNoContent());

        verify(employeeService).delete(1L);
    }

    private Employee buildEmployee(Long id, Long shiftId) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setName("Ana");
        employee.setTelephone("555");
        employee.setAddress("Street");
        employee.setBankAccount("IBAN");
        employee.setMonthlyHours(160.0);
        employee.setSalary(2500.0);
        employee.setShift(buildShift(shiftId));
        return employee;
    }

    private Shift buildShift(Long id) {
        Shift shift = new Shift();
        shift.setId(id);
        return shift;
    }
}