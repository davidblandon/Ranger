package com.example.productionservice.service.material.impl;

import com.example.productionservice.domain.material.Material;
import com.example.productionservice.service.material.MaterialService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MaterialServiceImpl implements MaterialService {
    private final List<Material> materials = new ArrayList<>();

    @Override
    public Material createMaterial(Material material) {
        materials.add(material);
        return material;
    }

    @Override
    public Material getMaterialById(int id) {
        Optional<Material> material = materials.stream()
            .filter(m -> m.getId() == id)
            .findFirst();
        return material.orElse(null);
    }

    @Override
    public List<Material> getAllMaterials() {
        return new ArrayList<>(materials);
    }

    @Override
    public Material updateMaterial(Material material) {
        for (int i = 0; i < materials.size(); i++) {
            if (materials.get(i).getId() == material.getId()) {
                materials.set(i, material);
                return material;
            }
        }
        return null;
    }

    @Override
    public void deleteMaterial(int id) {
        materials.removeIf(m -> m.getId() == id);
    }
}
