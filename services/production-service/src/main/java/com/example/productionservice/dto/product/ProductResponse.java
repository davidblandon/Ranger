package com.example.productionservice.dto.product;

import java.util.List;

public record ProductResponse(
    Long id,
    String name,
    String type,
    String size,
    String color,
    List<Long> materialIds,
    double price,
    double cost,
    int stock
) {
}
