package com.example.productionservice.dto.order;

import com.example.productionservice.domain.order.OrderState;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Map;

public record OrderRequest(
    LocalDate date,
    @NotNull Long partnerId,
    @NotEmpty Map<Long, Integer> products,
    OrderState state
) {
}
