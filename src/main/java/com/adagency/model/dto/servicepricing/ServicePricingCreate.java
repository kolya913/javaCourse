package com.adagency.model.dto.servicepricing;

import com.adagency.model.dto.status.StatusView;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
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
	
	
	private List<StatusView> statusViews;
	
	private Long statusId;
}
