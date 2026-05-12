package com.example.productionservice.service.batch.impl;

import com.example.productionservice.domain.batch.Batch;
import com.example.productionservice.service.batch.BatchService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BatchServiceImpl implements BatchService {
    private final List<Batch> batches = new ArrayList<>();

    @Override
    public Batch createBatch(Batch batch) {
        batches.add(batch);
        return batch;
    }

    @Override
    public Batch getBatchById(int id) {
        Optional<Batch> batch = batches.stream()
            .filter(b -> b.getId() == id)
            .findFirst();
        return batch.orElse(null);
    }

    @Override
    public List<Batch> getAllBatches() {
        return new ArrayList<>(batches);
    }

    @Override
    public Batch updateBatch(Batch batch) {
        for (int i = 0; i < batches.size(); i++) {
            if (batches.get(i).getId() == batch.getId()) {
                batches.set(i, batch);
                return batch;
            }
        }
        return null;
    }

    @Override
    public void deleteBatch(int id) {
        batches.removeIf(b -> b.getId() == id);
    }
}
