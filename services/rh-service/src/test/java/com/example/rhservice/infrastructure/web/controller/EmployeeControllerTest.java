package com.example.rhservice.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EmployeeControllerTest extends AbstractApiIntegrationTest {

    @Test
    void adminCanListEmployees() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/employees").session(adminSession))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()")
                        .value(org.hamcrest.Matchers.greaterThanOrEqualTo(2)));
    }

    @Test
    void adminCanCreateEmployee() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/employees")
                        .session(adminSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Ana","username":"ana","password":"secret","telephone":"555","address":"Street","bankAccount":"IBAN","monthlyHours":160,"salary":2500,"shiftId":%d}
                                """.formatted(employeeShiftId)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("ana"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.shiftId").value(employeeShiftId.intValue()));
    }

    @Test
    void adminCanGetEmployeeById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/employees/{id}", otherEmployeeId).session(adminSession))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("employee2"));
    }

    @Test
    void adminCanUpdateEmployee() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/employees/{id}", otherEmployeeId)
                        .session(adminSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Employee Two Updated","username":"employee2","password":"secret","telephone":"555","address":"Street 2","bankAccount":"IBAN2","monthlyHours":180,"salary":2600,"shiftId":%d}
                                """.formatted(otherShiftId)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Employee Two Updated"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.shiftId").value(otherShiftId.intValue()));
    }

    @Test
    void adminCanDeleteEmployee() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/employees/{id}", otherEmployeeId).session(adminSession))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void employeeCanReadOwnProfile() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/employees/{id}", employeeId).session(employeeSession))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("employee1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.role").value("EMPLOYEE"));
    }

    @Test
    void employeeCannotReadAnotherProfile() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/employees/{id}", otherEmployeeId).session(employeeSession))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Forbidden"));
    }

    @Test
    void employeeCannotUpdateOwnProfile() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/employees/{id}", employeeId)
                        .session(employeeSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Employee One","username":"employee1","password":"newpass","telephone":"555","address":"Street","bankAccount":"IBAN","monthlyHours":160,"salary":2500,"shiftId":%d}
                                """.formatted(employeeShiftId)))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Forbidden"));
    }

    @Test
    void employeeCannotDeleteOwnProfile() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/employees/{id}", employeeId).session(employeeSession))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Forbidden"));
    }

    @Test
    void employeeCannotManageEmployees() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/employees")
                        .session(employeeSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Ana","username":"ana","password":"secret","telephone":"555","address":"Street","bankAccount":"IBAN","monthlyHours":160,"salary":2500,"shiftId":%d}
                                """.formatted(employeeShiftId)))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Forbidden"));
    }
}