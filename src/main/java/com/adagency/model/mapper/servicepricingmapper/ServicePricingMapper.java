package com.adagency.model.mapper.servicepricingmapper;

import com.adagency.model.dto.servicepricing.ServicePricingCreate;
import com.adagency.model.dto.servicepricing.ServicePricingEdit;
import com.adagency.model.dto.servicepricing.ServicePricingView;
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
				.statusId(servicePricing.getStatus().getId())
				.build();
	}

	public ServicePricingView fromServicePricingToServicePricingView(ServicePricing servicePricing){
		return ServicePricingView.builder()
				.serviceName(servicePricing.getServiceName())
				.circulation(servicePricing.getCirculation())
				.maxPeriodInDays(servicePricing.getMaxPeriodInDays())
				.minPeriodInDays(servicePricing.getMinPeriodInDays())
				.price(servicePricing.getPrice())
				.id(servicePricing.getId())
				.build();
	}
	
	public void fromServicePricingEditToServicePricing(ServicePricingEdit servicePricingEdit, ServicePricing servicePricing){
		servicePricing.setServiceName(servicePricingEdit.getServiceName());
		servicePricing.setCirculation(servicePricing.getCirculation());
		servicePricing.setMinPeriodInDays(servicePricingEdit.getMinPeriodInDays());
		servicePricing.setMaxPeriodInDays(servicePricingEdit.getMaxPeriodInDays());
	}
	
	public ServicePricing fromServicePricingEditToNewServicePricing(ServicePricingEdit servicePricingEdit){
		return ServicePricing.builder()
				.serviceName(servicePricingEdit.getServiceName())
				.price(servicePricingEdit.getPrice())
				.minPeriodInDays(servicePricingEdit.getMinPeriodInDays())
				.maxPeriodInDays(servicePricingEdit.getMaxPeriodInDays())
				.circulation(servicePricingEdit.getCirculation())
				.build();
	}

}
