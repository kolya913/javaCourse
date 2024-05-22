package com.adagency.model.dto.servicepricing;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ServicePricingCreate {
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

	@NotEmpty
	private String advertisementType;
	
	@NotNull
	@NotEmpty
	private Long serviceId;
}
