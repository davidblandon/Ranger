package com.example.productionservice.repository.order;

import com.example.productionservice.domain.order.Order;
import java.util.List;

public interface OrderRepository {
    Order save(Order order);
    Order findById(int id);
    List<Order> findAll();
    Order update(Order order);
    void deleteById(int id);
}
