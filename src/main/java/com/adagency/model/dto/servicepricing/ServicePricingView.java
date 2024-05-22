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
	
	@NotNull
	@NotEmpty
	private String serviceName;
	
	@NotNull
	@NotEmpty
	private Float price;
	
	@NotNull
	@NotEmpty
	private Integer minPeriodInDays;
	
	@NotNull
	@NotEmpty
	private Integer maxPeriodInDays;
	
	@NotNull
	@NotEmpty
	private Integer circulation;
	
	
	@NotNull
	@NotEmpty
	private Long serviceId;
}
