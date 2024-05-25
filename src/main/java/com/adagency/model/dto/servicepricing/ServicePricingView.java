package com.adagency.model.dto.servicepricing;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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

}
