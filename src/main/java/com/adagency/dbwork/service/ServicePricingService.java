package com.adagency.dbwork.service;

import com.adagency.dbwork.jparepo.ServicePricingRepository;
import com.adagency.model.dto.servicepricing.ServicePricingCreate;
import com.adagency.model.dto.servicepricing.ServicePricingCreateEditList;
import com.adagency.model.dto.servicepricing.ServicePricingEdit;
import com.adagency.model.entity.Service;
import com.adagency.model.entity.ServicePricing;
import com.adagency.model.entity.Status;
import com.adagency.model.mapper.servicepricingmapper.ServicePricingMapper;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
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
	public void createUpdate(ServicePricingCreateEditList servicePricingCreateEditList) {
		Optional<Service> service = serviceService.getServiceById(servicePricingCreateEditList.getServiceId());
		if(!service.isPresent()){
			throw new EntityNotFoundException("ServiceNotFound");
		}else{
			if(servicePricingCreateEditList.getServicePricingCreateList() != null && !servicePricingCreateEditList.getServicePricingCreateList().isEmpty()) {
				List<ServicePricingCreate> servicePricingCreates = servicePricingCreateEditList.getServicePricingCreateList();
				for(ServicePricingCreate servicePricingCreate : servicePricingCreates){
					if(servicePricingCreate.getPrice() != null && servicePricingCreate.getServiceName() != null){
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
			if(servicePricingCreateEditList.getServicePricingEditList() != null && !servicePricingCreateEditList.getServicePricingEditList().isEmpty()){
				for(ServicePricingEdit servicePricingEdit : servicePricingCreateEditList.getServicePricingEditList()){
					Optional<ServicePricing> servicePricing = servicePricingRepository.findById(servicePricingEdit.getSerPriId());
						if(servicePricing.get().getPrice() == servicePricingEdit.getPrice()){
							if(servicePricing.get().getStatus().getId() != servicePricingEdit.getStatusId()){
								service.get().setStatus(statusService.findById(servicePricingEdit.getStatusId()).get());
							}
							servicePricingMapper.fromServicePricingEditToServicePricing(servicePricingEdit, servicePricing.get());
							servicePricingRepository.save(servicePricing.get());
						}else{
							ServicePricing newServicePricing = servicePricingMapper.fromServicePricingEditToNewServicePricing(servicePricingEdit);
							newServicePricing.setService(serviceService.getServiceById(servicePricingCreateEditList.getServiceId()).get());
							newServicePricing.setStatus(statusService.findById(servicePricingEdit.getStatusId()).get());
							newServicePricing.setPreviousId(servicePricingEdit.getSerPriId());
							servicePricing.get().setStatus(statusService.findByName("Outdated").get());
							servicePricingRepository.save(servicePricing.get());
							servicePricingRepository.save(newServicePricing);
						}
					}
			}
			
		}
	}
}
