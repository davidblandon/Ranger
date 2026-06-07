package com.example.productionservice.controller.batch;

import com.example.productionservice.dto.batch.BatchRequest;
import com.example.productionservice.dto.batch.BatchResponse;
import com.example.productionservice.service.batch.BatchService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/batches")
public class BatchController {

    private final BatchService batchService;

    public BatchController(BatchService batchService) {
        this.batchService = batchService;
    }

    @PostMapping
    public ResponseEntity<BatchResponse> create(@Valid @RequestBody BatchRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(batchService.createBatch(request));
    }

    @GetMapping("/{id}")
    public BatchResponse getById(@PathVariable Long id) {
        return batchService.getBatchById(id);
    }

    @GetMapping
    public List<BatchResponse> getAll() {
        return batchService.getAllBatches();
    }

    @PutMapping("/{id}")
    public BatchResponse update(@PathVariable Long id, @Valid @RequestBody BatchRequest request) {
        return batchService.updateBatch(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        batchService.deleteBatch(id);
        return ResponseEntity.noContent().build();
    }
}
