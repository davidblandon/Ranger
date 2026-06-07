package com.example.productionservice.service.order;

import com.example.productionservice.dto.order.OrderRequest;
import com.example.productionservice.dto.order.OrderResponse;
import java.util.List;

public interface OrderService {
    OrderResponse createOrder(OrderRequest request);
    OrderResponse getOrderById(Long id);
    List<OrderResponse> getAllOrders();
    OrderResponse updateOrder(Long id, OrderRequest request);
    void deleteOrder(Long id);
}
