package com.example.productionservice.service.product;

import com.example.productionservice.dto.product.ProductRequest;
import com.example.productionservice.dto.product.ProductResponse;
import java.util.List;

public interface ProductService {
    ProductResponse createProduct(ProductRequest request);
    ProductResponse getProductById(Long id);
    List<ProductResponse> getAllProducts();
    ProductResponse updateProduct(Long id, ProductRequest request);
    void deleteProduct(Long id);
}
