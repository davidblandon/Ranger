package com.example.productionservice.repository.partner;

import com.example.productionservice.domain.partner.Partner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartnerRepository extends JpaRepository<Partner, Long> {
}
