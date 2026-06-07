package com.example.productionservice.mapper.batch;

import com.example.productionservice.domain.batch.Batch;
import com.example.productionservice.dto.batch.BatchResponse;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;

@Component
public class BatchMapper {

    public BatchResponse toResponse(Batch batch) {
        return new BatchResponse(
            batch.getId(),
            batch.getArrivalDate(),
            new LinkedHashMap<>(batch.getMaterials()),
            batch.getCost()
        );
    }
}
