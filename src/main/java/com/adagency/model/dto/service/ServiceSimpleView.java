package com.adagency.model.dto.service;

import com.adagency.model.dto.servicepricing.ServicePricingView;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceSimpleView {
	private Long id;
	private String name;
	private List<ServicePricingView> servicePricingViewList;
}
