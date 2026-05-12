package com.example.productionservice.repository.product;

import com.example.productionservice.domain.product.Product;
import java.util.List;

public interface ProductRepository {
    Product save(Product product);
    Product findById(int id);
    List<Product> findAll();
    Product update(Product product);
    void deleteById(int id);
}
