package com.adagency.model.mapper.servicepricingmapper;

import com.adagency.model.dto.servicepricing.ServicePricingCreate;
import com.adagency.model.dto.servicepricing.ServicePricingEdit;
import com.adagency.model.entity.ServicePricing;
import org.springframework.stereotype.Component;

@Component
public class ServicePricingMapper {
	public ServicePricing fromServicePricingCreateToServicePricing(ServicePricingCreate servicePricingCreate){
		return ServicePricing.builder()
				.serviceName(servicePricingCreate.getServiceName())
				.price(servicePricingCreate.getPrice())
				.minPeriodInDays(servicePricingCreate.getMinPeriodInDays())
				.maxPeriodInDays(servicePricingCreate.getMaxPeriodInDays())
				.circulation(servicePricingCreate.getCirculation())
				.build();
	}
	
	public ServicePricingEdit fromServicePricingToServicePricingEdit(ServicePricing servicePricing){
		return ServicePricingEdit.builder()
				.serviceName(servicePricing.getServiceName())
				.circulation(servicePricing.getCirculation())
				.maxPeriodInDays(servicePricing.getMaxPeriodInDays())
				.minPeriodInDays(servicePricing.getMinPeriodInDays())
				.price(servicePricing.getPrice())
				.serPriId(servicePricing.getId())
				.build();
	}
}
