package com.example.productionservice.service.material;

import com.example.productionservice.dto.material.MaterialRequest;
import com.example.productionservice.dto.material.MaterialResponse;
import java.util.List;

public interface MaterialService {
    MaterialResponse createMaterial(MaterialRequest request);
    MaterialResponse getMaterialById(Long id);
    List<MaterialResponse> getAllMaterials();
    MaterialResponse updateMaterial(Long id, MaterialRequest request);
    void deleteMaterial(Long id);
}
