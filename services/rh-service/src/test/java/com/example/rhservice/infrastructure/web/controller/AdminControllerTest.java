package com.example.rhservice.infrastructure.web.controller;

import com.example.rhservice.application.port.in.AdminService;
import com.example.rhservice.domain.model.Admin;
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

@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @Test
    void shouldListAdmins() throws Exception {
        Admin admin = buildAdmin(1L);
        when(adminService.findAll()).thenReturn(List.of(admin));

        mockMvc.perform(get("/api/admins"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].permissions").value("ROLE_HR"));
    }

    @Test
    void shouldCreateAdmin() throws Exception {
        when(adminService.save(any(Admin.class))).thenAnswer(invocation -> {
            Admin admin = invocation.getArgument(0);
            admin.setId(2L);
            return admin;
        });

        mockMvc.perform(post("/api/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Laura","telephone":"555","address":"HQ","bankAccount":"ABC","permissions":"ROLE_HR"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.permissions").value("ROLE_HR"));
    }

    @Test
    void shouldGetAdminById() throws Exception {
        when(adminService.findById(1L)).thenReturn(java.util.Optional.of(buildAdmin(1L)));

        mockMvc.perform(get("/api/admins/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laura"));
    }

    @Test
    void shouldUpdateAdmin() throws Exception {
        when(adminService.findById(1L)).thenReturn(java.util.Optional.of(buildAdmin(1L)));
        when(adminService.save(any(Admin.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/api/admins/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Laura 2","telephone":"555","address":"HQ","bankAccount":"ABC","permissions":"ROLE_ADMIN"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laura 2"))
                .andExpect(jsonPath("$.permissions").value("ROLE_ADMIN"));
    }

    @Test
    void shouldDeleteAdmin() throws Exception {
        mockMvc.perform(delete("/api/admins/1"))
                .andExpect(status().isNoContent());

        verify(adminService).delete(1L);
    }

    private Admin buildAdmin(Long id) {
        Admin admin = new Admin();
        admin.setId(id);
        admin.setName("Laura");
        admin.setTelephone("555");
        admin.setAddress("HQ");
        admin.setBankAccount("ABC");
        admin.setPermissions("ROLE_HR");
        return admin;
    }
}