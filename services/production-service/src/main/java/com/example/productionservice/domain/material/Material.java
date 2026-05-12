package com.example.productionservice.domain.material;

public class Material {
    private int id;
    private String name;
    private String type;
    private String supplier;
    private double cost;
    private int stock;
    private int batchId;

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getSupplier() { return supplier; }
    public void setSupplier(String supplier) { this.supplier = supplier; }

    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public int getBatchId() { return batchId; }
    public void setBatchId(int batchId) { this.batchId = batchId; }
}
