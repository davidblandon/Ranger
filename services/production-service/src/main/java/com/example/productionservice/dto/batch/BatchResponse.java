package com.example.productionservice.dto.batch;

import java.time.LocalDate;
import java.util.Map;

public record BatchResponse(
    Long id,
    LocalDate arrivalDate,
    Map<Long, Integer> materials,
    double cost
) {
}
