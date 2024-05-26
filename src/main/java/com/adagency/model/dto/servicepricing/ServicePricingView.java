package com.adagency.model.dto.servicepricing;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServicePricingView {
	private Long id;
	
	private String serviceName;

	private Float price;
	
	private Integer minPeriodInDays;
	
	private Integer maxPeriodInDays;
	
	private Integer circulation;
	
	private Boolean selected = false;

}
