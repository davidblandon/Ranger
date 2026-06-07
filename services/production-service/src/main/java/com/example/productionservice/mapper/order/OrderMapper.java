package com.example.productionservice.mapper.order;

import com.example.productionservice.domain.order.Order;
import com.example.productionservice.dto.order.OrderResponse;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order) {
        return new OrderResponse(
            order.getId(),
            order.getDate(),
            order.getPartner() != null ? order.getPartner().getId() : null,
            new LinkedHashMap<>(order.getProducts()),
            order.getPrice(),
            order.getState(),
            order.getBenefice()
        );
    }
}
