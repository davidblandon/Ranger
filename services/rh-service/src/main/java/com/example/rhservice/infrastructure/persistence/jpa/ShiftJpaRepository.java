package com.example.rhservice.infrastructure.persistence.jpa;

import com.example.rhservice.domain.model.Shift;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShiftJpaRepository extends JpaRepository<Shift, Long> {
}