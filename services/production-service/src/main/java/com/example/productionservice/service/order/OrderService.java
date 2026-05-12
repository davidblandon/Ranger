package com.example.productionservice.service.order;

import com.example.productionservice.domain.order.Order;
import java.util.List;

public interface OrderService {
    Order createOrder(Order order);
    Order getOrderById(int id);
    List<Order> getAllOrders();
    Order updateOrder(Order order);
    void deleteOrder(int id);
}
