package com.example.rhservice.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AdminControllerTest extends AbstractApiIntegrationTest {

    @Test
    void adminCanListAdmins() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/admins").session(adminSession))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()")
                        .value(org.hamcrest.Matchers.greaterThanOrEqualTo(2)));
    }

    @Test
    void adminCanCreateAdmin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/admins")
                        .session(adminSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Laura","username":"laura","password":"secret","telephone":"555","address":"HQ","bankAccount":"ABC","permissions":"ROLE_HR"}
                                """))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("laura"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void adminCanGetAdminById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/admins/{id}", otherAdminId).session(adminSession))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("admin2"));
    }

    @Test
    void adminCanUpdateAdmin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/admins/{id}", otherAdminId)
                        .session(adminSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Admin Two Updated","username":"admin2","password":"secret","telephone":"555","address":"HQ 2","bankAccount":"ABC2","permissions":"ROLE_ADMIN"}
                                """))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Admin Two Updated"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.permissions").value("ROLE_ADMIN"));
    }

    @Test
    void adminCanDeleteAdmin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/admins/{id}", otherAdminId).session(adminSession))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void employeeCannotAccessAdminEndpoints() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/admins").session(employeeSession))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Forbidden"));
    }
}