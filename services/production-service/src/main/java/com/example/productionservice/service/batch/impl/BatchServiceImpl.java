package com.example.productionservice.service.batch.impl;

import com.example.productionservice.domain.batch.Batch;
import com.example.productionservice.domain.material.Material;
import com.example.productionservice.dto.batch.BatchRequest;
import com.example.productionservice.dto.batch.BatchResponse;
import com.example.productionservice.exception.BadRequestException;
import com.example.productionservice.exception.NotFoundException;
import com.example.productionservice.mapper.batch.BatchMapper;
import com.example.productionservice.repository.batch.BatchRepository;
import com.example.productionservice.repository.material.MaterialRepository;
import com.example.productionservice.service.batch.BatchService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class BatchServiceImpl implements BatchService {

    private final BatchRepository batchRepository;
    private final MaterialRepository materialRepository;
    private final BatchMapper batchMapper;

    public BatchServiceImpl(BatchRepository batchRepository,
                            MaterialRepository materialRepository,
                            BatchMapper batchMapper) {
        this.batchRepository = batchRepository;
        this.materialRepository = materialRepository;
        this.batchMapper = batchMapper;
    }

    @Override
    public BatchResponse createBatch(BatchRequest request) {
        Batch batch = new Batch();
        batch.setArrivalDate(request.arrivalDate());
        // Receiving a batch increases each material's stock by the received quantity.
        batch.setCost(applyMaterials(batch, request.materials(), true));

        return batchMapper.toResponse(batchRepository.save(batch));
    }

    @Override
    @Transactional(readOnly = true)
    public BatchResponse getBatchById(Long id) {
        return batchMapper.toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BatchResponse> getAllBatches() {
        return batchRepository.findAll().stream()
            .map(batchMapper::toResponse)
            .toList();
    }

    @Override
    public BatchResponse updateBatch(Long id, BatchRequest request) {
        Batch batch = findOrThrow(id);
        batch.setArrivalDate(request.arrivalDate());
        // Recompute composition and cost; stock is only adjusted on initial receipt.
        batch.setCost(applyMaterials(batch, request.materials(), false));

        return batchMapper.toResponse(batchRepository.save(batch));
    }

    @Override
    public void deleteBatch(Long id) {
        batchRepository.delete(findOrThrow(id));
    }

    /**
     * Validates the requested materials, sets them on the batch and returns the total cost
     * (Σ material.cost × quantity). When {@code adjustStock} is true each material's stock is
     * increased by the received quantity.
     */
    private double applyMaterials(Batch batch, Map<Long, Integer> requested, boolean adjustStock) {
        Map<Long, Integer> materials = new HashMap<>();
        double cost = 0;
        if (requested != null) {
            for (Map.Entry<Long, Integer> entry : requested.entrySet()) {
                Long materialId = entry.getKey();
                Integer quantity = entry.getValue();
                if (quantity == null || quantity <= 0) {
                    throw new BadRequestException(
                        "Quantity must be a positive integer for material: " + materialId);
                }
                Material material = materialRepository.findById(materialId)
                    .orElseThrow(() -> new NotFoundException("Material not found: " + materialId));
                if (adjustStock) {
                    material.setStock(material.getStock() + quantity);
                }
                cost += material.getCost() * quantity;
                materials.put(materialId, quantity);
            }
        }
        batch.setMaterials(materials);
        return cost;
    }

    private Batch findOrThrow(Long id) {
        return batchRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Batch not found: " + id));
    }
}
