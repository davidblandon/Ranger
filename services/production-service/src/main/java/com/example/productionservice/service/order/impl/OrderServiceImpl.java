package com.example.productionservice.service.order.impl;

import com.example.productionservice.domain.order.Order;
import com.example.productionservice.domain.order.OrderState;
import com.example.productionservice.domain.partner.Partner;
import com.example.productionservice.domain.product.Product;
import com.example.productionservice.dto.order.OrderRequest;
import com.example.productionservice.dto.order.OrderResponse;
import com.example.productionservice.exception.BadRequestException;
import com.example.productionservice.exception.NotFoundException;
import com.example.productionservice.mapper.order.OrderMapper;
import com.example.productionservice.repository.order.OrderRepository;
import com.example.productionservice.repository.partner.PartnerRepository;
import com.example.productionservice.repository.product.ProductRepository;
import com.example.productionservice.service.order.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final PartnerRepository partnerRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    public OrderServiceImpl(OrderRepository orderRepository,
                            PartnerRepository partnerRepository,
                            ProductRepository productRepository,
                            OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.partnerRepository = partnerRepository;
        this.productRepository = productRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    public OrderResponse createOrder(OrderRequest request) {
        Order order = new Order();
        order.setDate(request.date() != null ? request.date() : LocalDate.now());
        order.setPartner(resolvePartner(request.partnerId()));

        OrderState state = request.state() != null ? request.state() : OrderState.PENDING;
        order.setState(state);

        Totals totals = applyProducts(order, request.products(), state == OrderState.COMPLETED);
        order.setPrice(totals.price());
        order.setBenefice(totals.benefice());

        return orderMapper.toResponse(orderRepository.save(order));
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        return orderMapper.toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
            .map(orderMapper::toResponse)
            .toList();
    }

    @Override
    public OrderResponse updateOrder(Long id, OrderRequest request) {
        Order order = findOrThrow(id);

        // Restore stock consumed by the previous state before recomputing.
        if (order.getState() == OrderState.COMPLETED) {
            restoreStock(order.getProducts());
        }

        order.setDate(request.date() != null ? request.date() : order.getDate());
        order.setPartner(resolvePartner(request.partnerId()));

        OrderState state = request.state() != null ? request.state() : OrderState.PENDING;
        order.setState(state);

        Totals totals = applyProducts(order, request.products(), state == OrderState.COMPLETED);
        order.setPrice(totals.price());
        order.setBenefice(totals.benefice());

        return orderMapper.toResponse(orderRepository.save(order));
    }

    @Override
    public void deleteOrder(Long id) {
        Order order = findOrThrow(id);
        if (order.getState() == OrderState.COMPLETED) {
            restoreStock(order.getProducts());
        }
        orderRepository.delete(order);
    }

    /**
     * Validates the requested products, sets them on the order and returns price and benefice
     * (Σ product.price × quantity and Σ (product.price − product.cost) × quantity).
     * When {@code consumeStock} is true each product's stock is decremented by the quantity;
     * insufficient stock causes a BadRequestException.
     */
    private Totals applyProducts(Order order, Map<Long, Integer> requested, boolean consumeStock) {
        Map<Long, Integer> products = new HashMap<>();
        double price = 0, benefice = 0;
        if (requested != null) {
            for (Map.Entry<Long, Integer> entry : requested.entrySet()) {
                Long productId = entry.getKey();
                Integer quantity = entry.getValue();
                if (quantity == null || quantity <= 0) {
                    throw new BadRequestException(
                        "Quantity must be a positive integer for product: " + productId);
                }
                Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new NotFoundException("Product not found: " + productId));
                if (consumeStock) {
                    if (product.getStock() < quantity) {
                        throw new BadRequestException(
                            "Insufficient stock for product: " + productId
                            + " (requested " + quantity + ", available " + product.getStock() + ")");
                    }
                    product.setStock(product.getStock() - quantity);
                }
                price += product.getPrice() * quantity;
                benefice += (product.getPrice() - product.getCost()) * quantity;
                products.put(productId, quantity);
            }
        }
        order.setProducts(products);
        return new Totals(price, benefice);
    }

    private void restoreStock(Map<Long, Integer> products) {
        for (Map.Entry<Long, Integer> entry : products.entrySet()) {
            Product product = productRepository.findById(entry.getKey())
                .orElseThrow(() -> new NotFoundException("Product not found: " + entry.getKey()));
            product.setStock(product.getStock() + entry.getValue());
        }
    }

    private Partner resolvePartner(Long partnerId) {
        return partnerRepository.findById(partnerId)
            .orElseThrow(() -> new NotFoundException("Partner not found: " + partnerId));
    }

    private Order findOrThrow(Long id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Order not found: " + id));
    }

    private record Totals(double price, double benefice) {}
}
