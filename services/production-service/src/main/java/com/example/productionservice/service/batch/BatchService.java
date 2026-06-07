package com.example.productionservice.service.batch;

import com.example.productionservice.dto.batch.BatchRequest;
import com.example.productionservice.dto.batch.BatchResponse;
import java.util.List;

public interface BatchService {
    BatchResponse createBatch(BatchRequest request);
    BatchResponse getBatchById(Long id);
    List<BatchResponse> getAllBatches();
    BatchResponse updateBatch(Long id, BatchRequest request);
    void deleteBatch(Long id);
}
