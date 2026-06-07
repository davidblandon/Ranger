package com.example.productionservice.dto.batch;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Map;

public record BatchRequest(
    @NotNull LocalDate arrivalDate,
    Map<Long, Integer> materials
) {
}
