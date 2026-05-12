
package com.example.productionservice.domain.product;

import com.example.productionservice.domain.material.Material;
import java.util.List;

public class Product {
    private int id;
    private String name;
    private String type;
    private String size;
    private String color;
    private List<Material> materials;
    private double price;
    private double cost;
    private int stock;

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public List<Material> getMaterials() { return materials; }
    public void setMaterials(List<Material> materials) { this.materials = materials; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
}
