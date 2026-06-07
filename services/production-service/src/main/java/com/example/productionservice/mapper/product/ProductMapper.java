package com.example.productionservice.mapper.product;

import com.example.productionservice.domain.material.Material;
import com.example.productionservice.domain.product.Product;
import com.example.productionservice.dto.product.ProductResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductMapper {

    public ProductResponse toResponse(Product product) {
        List<Long> materialIds = product.getMaterials().stream()
            .map(Material::getId)
            .toList();
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getType(),
            product.getSize(),
            product.getColor(),
            materialIds,
            product.getPrice(),
            product.getCost(),
            product.getStock()
        );
    }
}
