package com.example.productionservice.domain.batch;

import com.example.productionservice.domain.material.Material;
import java.util.Date;
import java.util.List;

public class Batch {
    private int id;
    private Date arrivalDate;
    private List<Material> materials;
    private double cost;

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Date getArrivalDate() { return arrivalDate; }
    public void setArrivalDate(Date arrivalDate) { this.arrivalDate = arrivalDate; }

    public List<Material> getMaterials() { return materials; }
    public void setMaterials(List<Material> materials) { this.materials = materials; }

    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }
}
