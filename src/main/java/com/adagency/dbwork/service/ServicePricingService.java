package com.adagency.dbwork.service;

import com.adagency.dbwork.jparepo.ServicePricingRepository;
import com.adagency.model.dto.servicepricing.ServicePricingCreate;
import com.adagency.model.dto.servicepricing.ServicePricingCreateList;
import com.adagency.model.entity.Service;
import com.adagency.model.entity.ServicePricing;
import com.adagency.model.entity.Status;
import com.adagency.model.mapper.servicepricingmapper.ServicePricingMapper;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class ServicePricingService {
	private final ServicePricingRepository servicePricingRepository;
	private final ServiceService serviceService;
	private final ServicePricingMapper servicePricingMapper;
	private final StatusService statusService;
	
	@Autowired
	public ServicePricingService(ServicePricingRepository servicePricingRepository, ServiceService serviceService,
	                             ServicePricingMapper servicePricingMapper, StatusService statusService){
		this.servicePricingRepository = servicePricingRepository;
		this.serviceService = serviceService;
		this.servicePricingMapper = servicePricingMapper;
		this.statusService = statusService;
	}
	
	@Transactional
	public void createUpdate(ServicePricingCreateList servicePricingCreateList) {
		Optional<Service> service = serviceService.getServiceById(servicePricingCreateList.getServiceId());
		if(!service.isPresent()){
			throw new EntityNotFoundException("ServiceNotFound");
		}else{
			List<ServicePricingCreate> servicePricingCreates = servicePricingCreateList.getServicePricingCreateList();
			for(ServicePricingCreate servicePricingCreate : servicePricingCreates){
				ServicePricing servicePricing = servicePricingMapper.fromServicePricingCreateToServicePricing(servicePricingCreate);
				Optional<Status> status = statusService.findById(servicePricingCreate.getStatusId());
				if(!status.isPresent()){
					throw new EntityNotFoundException("StatusNotFound");
				}else{
					servicePricing.setStatus(status.get());
					servicePricing.setService(service.get());
					servicePricingRepository.save(servicePricing);
				}
			}
		}
	}
}
