package com.example.productionservice.service.material;

import com.example.productionservice.domain.material.Material;
import java.util.List;

public interface MaterialService {
    Material createMaterial(Material material);
    Material getMaterialById(int id);
    List<Material> getAllMaterials();
    Material updateMaterial(Material material);
    void deleteMaterial(int id);
}
