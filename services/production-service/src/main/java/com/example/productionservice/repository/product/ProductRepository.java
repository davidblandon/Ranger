package com.example.productionservice.repository.product;

import com.example.productionservice.domain.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    long countByStockLessThanEqual(int threshold);
}
