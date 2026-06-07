package com.example.productionservice.mapper.material;

import com.example.productionservice.domain.material.Material;
import com.example.productionservice.dto.material.MaterialResponse;
import org.springframework.stereotype.Component;

@Component
public class MaterialMapper {

    public MaterialResponse toResponse(Material material) {
        return new MaterialResponse(
            material.getId(),
            material.getName(),
            material.getType(),
            material.getPartner() != null ? material.getPartner().getId() : null,
            material.getCost(),
            material.getStock()
        );
    }
}
