package com.example.productionservice.dto.dashboard;

import com.example.productionservice.domain.order.OrderState;
import java.util.Map;

/** Aggregated, read-only snapshot of the production service for a dashboard view. */
public record DashboardResponse(
    long totalProducts,
    long totalMaterials,
    long totalOrders,
    long totalBatches,
    long totalPartners,
    Map<OrderState, Long> ordersByState,
    double completedRevenue,
    double completedBenefice,
    long outOfStockProducts
) {
}
