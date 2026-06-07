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
class ShiftControllerTest extends AbstractApiIntegrationTest {

    @Test
    void adminCanListShifts() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/shifts").session(adminSession))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()")
                        .value(org.hamcrest.Matchers.greaterThanOrEqualTo(2)));
    }

    @Test
    void adminCanCreateShift() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/shifts")
                        .session(adminSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"monday":[{"start":"08:00:00","end":"12:00:00"}],"tuesday":[],"wednesday":[],"thursday":[],"friday":[],"saturday":[],"sunday":[]}
                                """))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.monday[0].start").value("08:00:00"));
    }

    @Test
    void adminCanGetShiftById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/shifts/{id}", otherShiftId).session(adminSession))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(otherShiftId.intValue()));
    }

    @Test
    void adminCanUpdateShift() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/shifts/{id}", otherShiftId)
                        .session(adminSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"monday":[],"tuesday":[],"wednesday":[],"thursday":[],"friday":[],"saturday":[],"sunday":[]}
                                """))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(otherShiftId.intValue()));
    }

    @Test
    void adminCanDeleteShift() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/shifts/{id}", otherShiftId).session(adminSession))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void employeeCanListOwnShift() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/shifts").session(employeeSession))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()")
                        .value(org.hamcrest.Matchers.greaterThanOrEqualTo(1)));
    }

    @Test
    void employeeCanReadOwnShift() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/shifts/{id}", employeeShiftId).session(employeeSession))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(employeeShiftId.intValue()));
    }

    @Test
    void employeeCannotReadAnotherShift() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/shifts/{id}", otherShiftId).session(employeeSession))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Forbidden"));
    }

    @Test
    void employeeCannotCreateShift() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/shifts")
                        .session(employeeSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"monday":[{"start":"08:00:00","end":"12:00:00"}],"tuesday":[],"wednesday":[],"thursday":[],"friday":[],"saturday":[],"sunday":[]}
                                """))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Forbidden"));
    }

    @Test
    void employeeCannotUpdateShift() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/shifts/{id}", otherShiftId)
                        .session(employeeSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"monday":[],"tuesday":[],"wednesday":[],"thursday":[],"friday":[],"saturday":[],"sunday":[]}
                                """))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Forbidden"));
    }

    @Test
    void employeeCannotDeleteShift() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/shifts/{id}", otherShiftId).session(employeeSession))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Forbidden"));
    }
}