package com.example.productionservice.service.material.impl;

import com.example.productionservice.domain.material.Material;
import com.example.productionservice.domain.partner.Partner;
import com.example.productionservice.dto.material.MaterialRequest;
import com.example.productionservice.dto.material.MaterialResponse;
import com.example.productionservice.exception.NotFoundException;
import com.example.productionservice.mapper.material.MaterialMapper;
import com.example.productionservice.repository.material.MaterialRepository;
import com.example.productionservice.repository.partner.PartnerRepository;
import com.example.productionservice.service.material.MaterialService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MaterialServiceImpl implements MaterialService {

    private final MaterialRepository materialRepository;
    private final PartnerRepository partnerRepository;
    private final MaterialMapper materialMapper;

    public MaterialServiceImpl(MaterialRepository materialRepository,
                               PartnerRepository partnerRepository,
                               MaterialMapper materialMapper) {
        this.materialRepository = materialRepository;
        this.partnerRepository = partnerRepository;
        this.materialMapper = materialMapper;
    }

    @Override
    public MaterialResponse createMaterial(MaterialRequest request) {
        Material material = new Material();
        apply(material, request);
        return materialMapper.toResponse(materialRepository.save(material));
    }

    @Override
    @Transactional(readOnly = true)
    public MaterialResponse getMaterialById(Long id) {
        return materialMapper.toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaterialResponse> getAllMaterials() {
        return materialRepository.findAll().stream()
            .map(materialMapper::toResponse)
            .toList();
    }

    @Override
    public MaterialResponse updateMaterial(Long id, MaterialRequest request) {
        Material material = findOrThrow(id);
        apply(material, request);
        return materialMapper.toResponse(materialRepository.save(material));
    }

    @Override
    public void deleteMaterial(Long id) {
        materialRepository.delete(findOrThrow(id));
    }

    private void apply(Material material, MaterialRequest request) {
        material.setName(request.name());
        material.setType(request.type());
        material.setCost(request.cost());
        material.setStock(request.stock());
        material.setPartner(resolvePartner(request.partnerId()));
    }

    private Partner resolvePartner(Long partnerId) {
        if (partnerId == null) {
            return null;
        }
        return partnerRepository.findById(partnerId)
            .orElseThrow(() -> new NotFoundException("Partner not found: " + partnerId));
    }

    private Material findOrThrow(Long id) {
        return materialRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Material not found: " + id));
    }
}
