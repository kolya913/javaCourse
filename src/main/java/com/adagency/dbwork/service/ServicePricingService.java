package com.adagency.dbwork.service;

import com.adagency.dbwork.jparepo.ServicePricingRepository;
import org.springframework.beans.factory.annotation.Autowired;

@org.springframework.stereotype.Service
public class ServicePricingService {
	private final ServicePricingRepository servicePricingRepository;
	
	@Autowired
	public ServicePricingService(ServicePricingRepository servicePricingRepository){
		this.servicePricingRepository = servicePricingRepository;
	}
	
	
}
