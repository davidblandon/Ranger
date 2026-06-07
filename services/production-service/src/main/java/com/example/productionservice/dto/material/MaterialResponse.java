package com.example.productionservice.dto.material;

public record MaterialResponse(
    Long id,
    String name,
    String type,
    Long partnerId,
    double cost,
    int stock
) {
}
