package com.example.productionservice.service.batch;

import com.example.productionservice.domain.material.Material;
import com.example.productionservice.dto.batch.BatchRequest;
import com.example.productionservice.dto.batch.BatchResponse;
import com.example.productionservice.mapper.batch.BatchMapper;
import com.example.productionservice.repository.batch.BatchRepository;
import com.example.productionservice.repository.material.MaterialRepository;
import com.example.productionservice.service.batch.impl.BatchServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BatchServiceImplTest {

    @Mock BatchRepository batchRepository;
    @Mock MaterialRepository materialRepository;
    @Spy BatchMapper batchMapper;
    @InjectMocks BatchServiceImpl batchService;

    @Test
    void createSumsCostByQuantityAndIncrementsMaterialStock() {
        Material m1 = material(1L, 10.0, 0);
        Material m2 = material(2L, 25.0, 2);
        when(materialRepository.findById(1L)).thenReturn(Optional.of(m1));
        when(materialRepository.findById(2L)).thenReturn(Optional.of(m2));
        when(batchRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // 3 units of m1 and 1 unit of m2.
        Map<Long, Integer> requested = new LinkedHashMap<>();
        requested.put(1L, 3);
        requested.put(2L, 1);

        BatchResponse response = batchService.createBatch(
            new BatchRequest(LocalDate.now(), requested));

        assertThat(response.cost()).isEqualTo(55.0);   // 10*3 + 25*1
        assertThat(response.materials()).containsEntry(1L, 3).containsEntry(2L, 1);
        assertThat(m1.getStock()).isEqualTo(3);   // received +3
        assertThat(m2.getStock()).isEqualTo(3);   // 2 + 1
    }

    private Material material(Long id, double cost, int stock) {
        Material m = new Material();
        m.setId(id);
        m.setCost(cost);
        m.setStock(stock);
        return m;
    }
}
