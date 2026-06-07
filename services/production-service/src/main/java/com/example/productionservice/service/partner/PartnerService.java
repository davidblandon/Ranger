package com.example.productionservice.service.partner;

import com.example.productionservice.dto.partner.PartnerRequest;
import com.example.productionservice.dto.partner.PartnerResponse;
import java.util.List;

public interface PartnerService {
    PartnerResponse createPartner(PartnerRequest request);
    PartnerResponse getPartnerById(Long id);
    List<PartnerResponse> getAllPartners();
    PartnerResponse updatePartner(Long id, PartnerRequest request);
    void deletePartner(Long id);
}
