package com.example.productionservice.domain.material;

import com.example.productionservice.domain.partner.Partner;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "materials")
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type;

    @ManyToOne
    @JoinColumn(name = "partner_id")
    private Partner partner;

    private double cost;
    private int stock;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Partner getPartner() { return partner; }
    public void setPartner(Partner partner) { this.partner = partner; }

    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
}
