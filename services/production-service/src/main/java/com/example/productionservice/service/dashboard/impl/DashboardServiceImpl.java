package com.example.productionservice.service.dashboard.impl;

import com.example.productionservice.domain.order.OrderState;
import com.example.productionservice.dto.dashboard.DashboardResponse;
import com.example.productionservice.repository.batch.BatchRepository;
import com.example.productionservice.repository.material.MaterialRepository;
import com.example.productionservice.repository.order.OrderRepository;
import com.example.productionservice.repository.partner.PartnerRepository;
import com.example.productionservice.repository.product.ProductRepository;
import com.example.productionservice.service.dashboard.DashboardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final ProductRepository productRepository;
    private final MaterialRepository materialRepository;
    private final OrderRepository orderRepository;
    private final BatchRepository batchRepository;
    private final PartnerRepository partnerRepository;

    public DashboardServiceImpl(ProductRepository productRepository,
                                MaterialRepository materialRepository,
                                OrderRepository orderRepository,
                                BatchRepository batchRepository,
                                PartnerRepository partnerRepository) {
        this.productRepository = productRepository;
        this.materialRepository = materialRepository;
        this.orderRepository = orderRepository;
        this.batchRepository = batchRepository;
        this.partnerRepository = partnerRepository;
    }

    @Override
    public DashboardResponse getSummary() {
        Map<OrderState, Long> ordersByState = new EnumMap<>(OrderState.class);
        for (OrderState state : OrderState.values()) {
            ordersByState.put(state, orderRepository.countByState(state));
        }

        return new DashboardResponse(
            productRepository.count(),
            materialRepository.count(),
            orderRepository.count(),
            batchRepository.count(),
            partnerRepository.count(),
            ordersByState,
            orderRepository.sumPriceByState(OrderState.COMPLETED),
            orderRepository.sumBeneficeByState(OrderState.COMPLETED),
            productRepository.countByStockLessThanEqual(0)
        );
    }
}
