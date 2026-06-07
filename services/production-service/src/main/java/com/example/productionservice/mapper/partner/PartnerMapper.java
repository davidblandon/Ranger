package com.example.productionservice.mapper.partner;

import com.example.productionservice.domain.partner.Partner;
import com.example.productionservice.dto.partner.PartnerRequest;
import com.example.productionservice.dto.partner.PartnerResponse;
import org.springframework.stereotype.Component;

@Component
public class PartnerMapper {

    public Partner toEntity(PartnerRequest request) {
        Partner partner = new Partner();
        apply(partner, request);
        return partner;
    }

    public void apply(Partner partner, PartnerRequest request) {
        partner.setName(request.name());
        partner.setRol(request.rol());
        partner.setBankNumber(request.bankNumber());
        partner.setTelephone(request.telephone());
    }

    public PartnerResponse toResponse(Partner partner) {
        return new PartnerResponse(
            partner.getId(),
            partner.getName(),
            partner.getRol(),
            partner.getBankNumber(),
            partner.getTelephone()
        );
    }
}
