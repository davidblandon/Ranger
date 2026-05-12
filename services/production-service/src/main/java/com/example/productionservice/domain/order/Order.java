package com.example.productionservice.domain.order;

import com.example.productionservice.domain.product.Product;
import java.util.Date;
import java.util.List;

public class Order {
    private int id;
    private Date date;
    private String client;
    private double price;
    private List<Product> products;
    private String state;

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public String getClient() { return client; }
    public void setClient(String client) { this.client = client; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public List<Product> getProducts() { return products; }
    public void setProducts(List<Product> products) { this.products = products; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
}
