package com.example.productionservice.service.partner.impl;

import com.example.productionservice.domain.partner.Partner;
import com.example.productionservice.dto.partner.PartnerRequest;
import com.example.productionservice.dto.partner.PartnerResponse;
import com.example.productionservice.exception.NotFoundException;
import com.example.productionservice.mapper.partner.PartnerMapper;
import com.example.productionservice.repository.partner.PartnerRepository;
import com.example.productionservice.service.partner.PartnerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PartnerServiceImpl implements PartnerService {

    private final PartnerRepository partnerRepository;
    private final PartnerMapper partnerMapper;

    public PartnerServiceImpl(PartnerRepository partnerRepository, PartnerMapper partnerMapper) {
        this.partnerRepository = partnerRepository;
        this.partnerMapper = partnerMapper;
    }

    @Override
    public PartnerResponse createPartner(PartnerRequest request) {
        Partner partner = partnerMapper.toEntity(request);
        return partnerMapper.toResponse(partnerRepository.save(partner));
    }

    @Override
    @Transactional(readOnly = true)
    public PartnerResponse getPartnerById(Long id) {
        return partnerMapper.toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PartnerResponse> getAllPartners() {
        return partnerRepository.findAll().stream()
            .map(partnerMapper::toResponse)
            .toList();
    }

    @Override
    public PartnerResponse updatePartner(Long id, PartnerRequest request) {
        Partner partner = findOrThrow(id);
        partnerMapper.apply(partner, request);
        return partnerMapper.toResponse(partnerRepository.save(partner));
    }

    @Override
    public void deletePartner(Long id) {
        partnerRepository.delete(findOrThrow(id));
    }

    private Partner findOrThrow(Long id) {
        return partnerRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Partner not found: " + id));
    }
}
