package com.example.rhservice.infrastructure.persistence.jpa;

import com.example.rhservice.domain.model.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayrollJpaRepository extends JpaRepository<Payroll, Long> {
}