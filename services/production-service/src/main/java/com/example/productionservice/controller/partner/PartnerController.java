package com.example.productionservice.controller.partner;

import com.example.productionservice.dto.partner.PartnerRequest;
import com.example.productionservice.dto.partner.PartnerResponse;
import com.example.productionservice.service.partner.PartnerService;
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
@RequestMapping("/api/partners")
public class PartnerController {

    private final PartnerService partnerService;

    public PartnerController(PartnerService partnerService) {
        this.partnerService = partnerService;
    }

    @PostMapping
    public ResponseEntity<PartnerResponse> create(@Valid @RequestBody PartnerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(partnerService.createPartner(request));
    }

    @GetMapping("/{id}")
    public PartnerResponse getById(@PathVariable Long id) {
        return partnerService.getPartnerById(id);
    }

    @GetMapping
    public List<PartnerResponse> getAll() {
        return partnerService.getAllPartners();
    }

    @PutMapping("/{id}")
    public PartnerResponse update(@PathVariable Long id, @Valid @RequestBody PartnerRequest request) {
        return partnerService.updatePartner(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        partnerService.deletePartner(id);
        return ResponseEntity.noContent().build();
    }
}
