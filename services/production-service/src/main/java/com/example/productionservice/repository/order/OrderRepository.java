package com.example.productionservice.repository.order;

import com.example.productionservice.domain.order.Order;
import com.example.productionservice.domain.order.OrderState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {

    long countByState(OrderState state);

    @Query("select coalesce(sum(o.price), 0) from Order o where o.state = :state")
    double sumPriceByState(@Param("state") OrderState state);

    @Query("select coalesce(sum(o.benefice), 0) from Order o where o.state = :state")
    double sumBeneficeByState(@Param("state") OrderState state);
}
