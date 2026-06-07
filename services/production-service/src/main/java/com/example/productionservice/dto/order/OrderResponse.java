package com.example.productionservice.dto.order;

import com.example.productionservice.domain.order.OrderState;
import java.time.LocalDate;
import java.util.Map;

public record OrderResponse(
    Long id,
    LocalDate date,
    Long partnerId,
    Map<Long, Integer> products,
    double price,
    OrderState state,
    double benefice
) {
}
