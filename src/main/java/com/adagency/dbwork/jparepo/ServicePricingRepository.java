package com.adagency.dbwork.jparepo;

import com.adagency.model.entity.ServicePricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicePricingRepository extends JpaRepository<ServicePricing, Long> {
}
