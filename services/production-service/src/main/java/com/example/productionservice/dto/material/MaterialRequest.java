package com.example.productionservice.dto.material;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record MaterialRequest(
    @NotBlank String name,
    String type,
    Long partnerId,
    @PositiveOrZero double cost,
    @PositiveOrZero int stock
) {
}
