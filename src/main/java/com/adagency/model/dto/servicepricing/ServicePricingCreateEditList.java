package com.adagency.model.dto.servicepricing;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServicePricingCreateEditList {
	private Long serviceId;
	private List<ServicePricingCreate> servicePricingCreateList;
	private List<ServicePricingEdit> servicePricingEditList;
}
