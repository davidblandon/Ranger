package com.example.productionservice.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;

public record ProductRequest(
    @NotBlank String name,
    String type,
    String size,
    String color,
    List<Long> materialIds,
    @PositiveOrZero double price,
    @PositiveOrZero double cost,
    @PositiveOrZero int stock
) {
}
