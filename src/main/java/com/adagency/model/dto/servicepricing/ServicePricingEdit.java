package com.adagency.model.dto.servicepricing;


import com.adagency.model.dto.status.StatusView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ServicePricingEdit {
	private Long serPriId;
	
	private String serviceName;
	
	private float price;
	
	private int minPeriodInDays;
	
	private int maxPeriodInDays;
	
	private int circulation;
	
	private StatusView statusView;
}
