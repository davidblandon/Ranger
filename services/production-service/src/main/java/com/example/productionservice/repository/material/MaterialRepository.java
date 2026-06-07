package com.example.productionservice.repository.material;

import com.example.productionservice.domain.material.Material;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialRepository extends JpaRepository<Material, Long> {
}
