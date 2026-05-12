package com.example.productionservice.service.product;

import com.example.productionservice.domain.product.Product;
import java.util.List;

public interface ProductService {
    Product createProduct(Product product);
    Product getProductById(int id);
    List<Product> getAllProducts();
    Product updateProduct(Product product);
    void deleteProduct(int id);
}
