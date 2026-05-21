package com.example.rhservice.infrastructure.persistence.jpa;

import com.example.rhservice.domain.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminJpaRepository extends JpaRepository<Admin, Long> {
}