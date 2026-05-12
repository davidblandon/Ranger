package com.example.productionservice.service.batch;

import com.example.productionservice.domain.batch.Batch;
import java.util.List;

public interface BatchService {
    Batch createBatch(Batch batch);
    Batch getBatchById(int id);
    List<Batch> getAllBatches();
    Batch updateBatch(Batch batch);
    void deleteBatch(int id);
}
