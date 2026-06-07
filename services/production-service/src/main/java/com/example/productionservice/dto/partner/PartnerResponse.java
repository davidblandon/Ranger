package com.example.productionservice.dto.partner;

public record PartnerResponse(
    Long id,
    String name,
    String rol,
    String bankNumber,
    String telephone
) {
}
