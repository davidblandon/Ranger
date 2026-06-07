package com.example.productionservice.domain.order;

import com.example.productionservice.domain.partner.Partner;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "partner_id")
    private Partner partner;

    private double price;

    // Products in this order, keyed by product id with the ordered quantity.
    @ElementCollection
    @CollectionTable(name = "order_products", joinColumns = @JoinColumn(name = "order_id"))
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    private Map<Long, Integer> products = new HashMap<>();

    @Enumerated(EnumType.STRING)
    private OrderState state;

    private double benefice;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Partner getPartner() { return partner; }
    public void setPartner(Partner partner) { this.partner = partner; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public Map<Long, Integer> getProducts() { return products; }
    public void setProducts(Map<Long, Integer> products) { this.products = products; }

    public OrderState getState() { return state; }
    public void setState(OrderState state) { this.state = state; }

    public double getBenefice() { return benefice; }
    public void setBenefice(double benefice) { this.benefice = benefice; }
}
