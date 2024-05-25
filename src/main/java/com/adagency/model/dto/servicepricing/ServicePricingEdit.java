package com.adagency.model.dto.servicepricing;


import com.adagency.model.dto.status.StatusView;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServicePricingEdit {
	private Long serPriId;
	
	private String serviceName;
	
	private Float price;
	
	private Integer minPeriodInDays;
	
	private Integer maxPeriodInDays;
	
	private Integer circulation;
	
	private StatusView statusView;
	private Long statusId;
}
