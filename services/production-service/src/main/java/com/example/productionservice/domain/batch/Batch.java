package com.example.productionservice.domain.batch;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "batches")
public class Batch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate arrivalDate;

    // Materials received in this batch, keyed by material id with the received quantity.
    @ElementCollection
    @CollectionTable(name = "batch_materials", joinColumns = @JoinColumn(name = "batch_id"))
    @MapKeyColumn(name = "material_id")
    @Column(name = "quantity")
    private Map<Long, Integer> materials = new HashMap<>();

    private double cost;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getArrivalDate() { return arrivalDate; }
    public void setArrivalDate(LocalDate arrivalDate) { this.arrivalDate = arrivalDate; }

    public Map<Long, Integer> getMaterials() { return materials; }
    public void setMaterials(Map<Long, Integer> materials) { this.materials = materials; }

    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }
}
