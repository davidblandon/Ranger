package com.example.productionservice.dto.partner;

import jakarta.validation.constraints.NotBlank;

public record PartnerRequest(
    @NotBlank String name,
    @NotBlank String rol,
    String bankNumber,
    String telephone
) {
}
