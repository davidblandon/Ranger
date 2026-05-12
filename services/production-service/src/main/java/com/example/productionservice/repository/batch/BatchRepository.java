package com.example.productionservice.repository.batch;

import com.example.productionservice.domain.batch.Batch;
import java.util.List;

public interface BatchRepository {
    Batch save(Batch batch);
    Batch findById(int id);
    List<Batch> findAll();
    Batch update(Batch batch);
    void deleteById(int id);
}
