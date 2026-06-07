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
class AuthControllerTest extends AbstractApiIntegrationTest {

    @Test
    void loginAsAdminReturnsUserInfoAndSession() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"admin","password":"supersecurepassword"}
                                """))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("admin"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void logoutClearsTheSession() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/logout").session(adminSession))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void secureEndpointWithoutLoginReturns401() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/admins"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    void loginWithWrongCredentialsReturnsBadRequestMessage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"admin","password":"wrong-password"}
                                """))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Bad Request"));
    }
}