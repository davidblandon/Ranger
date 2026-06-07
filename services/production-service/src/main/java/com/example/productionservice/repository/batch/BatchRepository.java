package com.example.productionservice.repository.batch;

import com.example.productionservice.domain.batch.Batch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BatchRepository extends JpaRepository<Batch, Long> {
}
