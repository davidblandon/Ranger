package com.example.productionservice.repository.material;

import com.example.productionservice.domain.material.Material;
import java.util.List;

public interface MaterialRepository {
    Material save(Material material);
    Material findById(int id);
    List<Material> findAll();
    Material update(Material material);
    void deleteById(int id);
}
