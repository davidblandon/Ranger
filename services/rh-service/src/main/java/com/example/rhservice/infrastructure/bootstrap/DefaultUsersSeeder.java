package com.example.rhservice.infrastructure.bootstrap;

import com.example.rhservice.domain.model.Admin;
import com.example.rhservice.domain.model.Employee;
import com.example.rhservice.domain.model.UserRole;
import com.example.rhservice.infrastructure.persistence.jpa.UserJpaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DefaultUsersSeeder implements CommandLineRunner {

    private static final String DEFAULT_ADMIN_USERNAME = "admin";
    private static final String DEFAULT_ADMIN_PASSWORD = "supersecurepassword";
    private static final String DEFAULT_EMPLOYEE_USERNAME = "employee1";
    private static final String DEFAULT_EMPLOYEE_PASSWORD = "supernormalpassword";

    private final UserJpaRepository userJpaRepository;
    private final PasswordEncoder passwordEncoder;

    public DefaultUsersSeeder(UserJpaRepository userJpaRepository,
                              PasswordEncoder passwordEncoder) {
        this.userJpaRepository = userJpaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedDefaultAdmin();
        seedDefaultEmployee();
    }

    private void seedDefaultAdmin() {
        if (userJpaRepository.findByUsername(DEFAULT_ADMIN_USERNAME).isPresent()) {
            return;
        }

        Admin admin = new Admin();
        admin.setName("Default Admin");
        admin.setUsername(DEFAULT_ADMIN_USERNAME);
        admin.setPassword(passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD));
        admin.setRole(UserRole.ADMIN);
        admin.setTelephone("000000000");
        admin.setAddress("System seed");
        admin.setBankAccount("N/A");
        admin.setPermissions("ROLE_ADMIN");

        userJpaRepository.save(admin);
    }

    private void seedDefaultEmployee() {
        if (userJpaRepository.findByUsername(DEFAULT_EMPLOYEE_USERNAME).isPresent()) {
            return;
        }

        Employee employee = new Employee();
        employee.setName("Default Employee");
        employee.setUsername(DEFAULT_EMPLOYEE_USERNAME);
        employee.setPassword(passwordEncoder.encode(DEFAULT_EMPLOYEE_PASSWORD));
        employee.setRole(UserRole.EMPLOYEE);
        employee.setTelephone("000000001");
        employee.setAddress("System seed");
        employee.setBankAccount("N/A");
        employee.setMonthlyHours(160.0);
        employee.setSalary(0.0);

        userJpaRepository.save(employee);
    }
}