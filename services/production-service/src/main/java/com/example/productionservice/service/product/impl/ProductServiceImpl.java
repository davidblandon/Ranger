package com.example.productionservice.service.product.impl;

import com.example.productionservice.domain.product.Product;
import com.example.productionservice.service.product.ProductService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductServiceImpl implements ProductService {
    private final List<Product> products = new ArrayList<>();

    @Override
    public Product createProduct(Product product) {
        products.add(product);
        return product;
    }

    @Override
    public Product getProductById(int id) {
        Optional<Product> product = products.stream()
            .filter(p -> p.getId() == id)
            .findFirst();
        return product.orElse(null);
    }

    @Override
    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    @Override
    public Product updateProduct(Product product) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == product.getId()) {
                products.set(i, product);
                return product;
            }
        }
        return null;
    }

    @Override
    public void deleteProduct(int id) {
        products.removeIf(p -> p.getId() == id);
    }
}
