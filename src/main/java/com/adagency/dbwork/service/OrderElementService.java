package com.adagency.dbwork.service;

import com.adagency.dbwork.jparepo.OrderElementRepository;
import com.adagency.model.dto.servicepricing.ServicePricingView;
import com.adagency.model.entity.Order;
import com.adagency.model.entity.OrderElement;
import com.adagency.model.entity.ServicePricing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderElementService {
	private final OrderElementRepository orderElementRepository;
	private final ServicePricingService servicePricingService;
	
	@Autowired
	public OrderElementService(OrderElementRepository orderElementRepository, ServicePricingService servicePricingService){
		this.orderElementRepository = orderElementRepository;
		this.servicePricingService = servicePricingService;
	}
	
	
	@Transactional
	public List<OrderElement> createOrderElementByListServicePricingView(List<Long> servicePricingViewList, Order order) {
		return servicePricingViewList.stream()
				.map(servicePricingView -> {
					OrderElement orderElement = new OrderElement();
					orderElement.setOrder(order);
					orderElement.setServicePricing(servicePricingService.getServicePricingById(servicePricingView).orElseThrow());
					return orderElementRepository.save(orderElement);
				})
				.collect(Collectors.toList());
	}
	
	
}
