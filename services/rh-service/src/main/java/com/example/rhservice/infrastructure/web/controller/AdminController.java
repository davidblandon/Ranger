package com.example.rhservice.infrastructure.web.controller;

import com.example.rhservice.application.port.in.AdminService;
import com.example.rhservice.domain.model.Admin;
import com.example.rhservice.infrastructure.web.dto.AdminRequest;
import com.example.rhservice.infrastructure.web.dto.AdminResponse;
import com.example.rhservice.infrastructure.web.security.CurrentUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
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
@RequestMapping("/api/admins")
@Tag(name = "Admins")
public class AdminController {

    private final AdminService adminService;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserService currentUserService;

    public AdminController(AdminService adminService, PasswordEncoder passwordEncoder, CurrentUserService currentUserService) {
        this.adminService = adminService;
        this.passwordEncoder = passwordEncoder;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public List<AdminResponse> findAll(Authentication authentication) {
        currentUserService.requireAdmin(authentication);
        return adminService.findAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public AdminResponse findById(@PathVariable Long id, Authentication authentication) {
        currentUserService.requireAdmin(authentication);
        return toResponse(loadAdmin(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AdminResponse create(@Valid @RequestBody AdminRequest request, Authentication authentication) {
        currentUserService.requireAdmin(authentication);
        return toResponse(adminService.save(toEntity(new Admin(), request)));
    }

    @PutMapping("/{id}")
    public AdminResponse update(@PathVariable Long id, @Valid @RequestBody AdminRequest request, Authentication authentication) {
        currentUserService.requireAdmin(authentication);
        Admin admin = loadAdmin(id);
        return toResponse(adminService.save(toEntity(admin, request)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, Authentication authentication) {
        currentUserService.requireAdmin(authentication);
        adminService.delete(id);
    }

    private Admin loadAdmin(Long id) {
        return adminService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found"));
    }

    private Admin toEntity(Admin admin, AdminRequest request) {
        admin.setName(request.name());
        admin.setUsername(request.username());
        admin.setPassword(passwordEncoder.encode(request.password()));
        admin.setRole(com.example.rhservice.domain.model.UserRole.ADMIN);
        admin.setTelephone(request.telephone());
        admin.setAddress(request.address());
        admin.setBankAccount(request.bankAccount());
        admin.setPermissions(request.permissions());
        return admin;
    }

    private AdminResponse toResponse(Admin admin) {
        return new AdminResponse(
                admin.getId(),
                admin.getName(),
            admin.getUsername(),
            admin.getRole().name(),
                admin.getTelephone(),
                admin.getAddress(),
                admin.getBankAccount(),
                admin.getPermissions()
        );
    }
}