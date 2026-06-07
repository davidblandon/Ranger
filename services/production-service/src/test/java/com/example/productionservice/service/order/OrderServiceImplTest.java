package com.example.productionservice.service.order;

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
import com.example.productionservice.service.order.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock OrderRepository orderRepository;
    @Mock PartnerRepository partnerRepository;
    @Mock ProductRepository productRepository;
    @Spy OrderMapper orderMapper;
    @InjectMocks OrderServiceImpl orderService;

    private Partner partner;
    private Product productA;
    private Product productB;

    @BeforeEach
    void setUp() {
        partner = new Partner();
        partner.setId(1L);

        productA = product(10L, 100.0, 60.0, 5);
        productB = product(11L, 50.0, 20.0, 3);
    }

    @Test
    void createComputesPriceAndBeneficeByQuantityAndKeepsStockWhenPending() {
        stubLookups();
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // 2 × productA + 1 × productB
        OrderResponse response = orderService.createOrder(
            new OrderRequest(null, 1L, Map.of(10L, 2, 11L, 1), OrderState.PENDING));

        assertThat(response.price()).isEqualTo(250.0);    // 100*2 + 50*1
        assertThat(response.benefice()).isEqualTo(110.0); // (100-60)*2 + (50-20)*1
        assertThat(productA.getStock()).isEqualTo(5);     // untouched while PENDING
        assertThat(productB.getStock()).isEqualTo(3);
    }

    @Test
    void createConsumesStockByQuantityWhenCompleted() {
        stubLookups();
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // 2 × productA + 1 × productB
        orderService.createOrder(new OrderRequest(null, 1L, Map.of(10L, 2, 11L, 1), OrderState.COMPLETED));

        assertThat(productA.getStock()).isEqualTo(3); // 5 - 2
        assertThat(productB.getStock()).isEqualTo(2); // 3 - 1
    }

    @Test
    void createRejectsCompletedOrderWhenStockInsufficient() {
        productA.setStock(1);
        when(partnerRepository.findById(1L)).thenReturn(Optional.of(partner));
        when(productRepository.findById(10L)).thenReturn(Optional.of(productA));

        // Requesting 3 but only 1 in stock.
        assertThatThrownBy(() ->
            orderService.createOrder(new OrderRequest(null, 1L, Map.of(10L, 3), OrderState.COMPLETED)))
            .isInstanceOf(BadRequestException.class);
    }

    @Test
    void createFailsWhenPartnerMissing() {
        when(partnerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
            orderService.createOrder(new OrderRequest(null, 99L, Map.of(10L, 1), OrderState.PENDING)))
            .isInstanceOf(NotFoundException.class);
    }

    private void stubLookups() {
        when(partnerRepository.findById(1L)).thenReturn(Optional.of(partner));
        when(productRepository.findById(10L)).thenReturn(Optional.of(productA));
        when(productRepository.findById(11L)).thenReturn(Optional.of(productB));
    }

    private Product product(Long id, double price, double cost, int stock) {
        Product p = new Product();
        p.setId(id);
        p.setPrice(price);
        p.setCost(cost);
        p.setStock(stock);
        return p;
    }
}
