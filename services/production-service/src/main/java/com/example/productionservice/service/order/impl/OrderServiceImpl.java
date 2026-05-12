package com.example.productionservice.service.order.impl;

import com.example.productionservice.domain.order.Order;
import com.example.productionservice.service.order.OrderService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderServiceImpl implements OrderService {
    private final List<Order> orders = new ArrayList<>();

    @Override
    public Order createOrder(Order order) {
        orders.add(order);
        return order;
    }

    @Override
    public Order getOrderById(int id) {
        Optional<Order> order = orders.stream()
            .filter(o -> o.getId() == id)
            .findFirst();
        return order.orElse(null);
    }

    @Override
    public List<Order> getAllOrders() {
        return new ArrayList<>(orders);
    }

    @Override
    public Order updateOrder(Order order) {
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getId() == order.getId()) {
                orders.set(i, order);
                return order;
            }
        }
        return null;
    }

    @Override
    public void deleteOrder(int id) {
        orders.removeIf(o -> o.getId() == id);
    }
}
