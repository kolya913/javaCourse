package com.adagency.dbwork.service;

import com.adagency.dbwork.jparepo.OrderRepository;
import com.adagency.model.dto.category.ClientSimpleCategory;
import com.adagency.model.dto.order.OrderCreate;
import com.adagency.model.dto.order.OrderElementCreate;
import com.adagency.model.dto.order.OrderElementCreateList;
import com.adagency.model.dto.order.OrderView;
import com.adagency.model.dto.service.ServiceSimpleView;
import com.adagency.model.dto.servicepricing.ServicePricingView;
import com.adagency.model.entity.Client;
import com.adagency.model.entity.Order;
import com.adagency.model.entity.OrderElement;
import com.adagency.model.mapper.servicepricingmapper.ServicePricingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {
	private final OrderRepository orderRepository;
	private final ClientService clientService;
	private final OrderStatusService orderStatusService;
	private final ServicePricingMapper servicePricingMapper;
	
	@Autowired
	private OrderElementService orderElementService ;
	
	@Autowired
	public OrderService(OrderRepository orderRepository, ClientService clientService,
	                    OrderStatusService orderStatusService, ServicePricingMapper servicePricingMapper) {
		this.orderRepository = orderRepository;
		this.clientService = clientService;
		this.orderStatusService = orderStatusService;
		this.servicePricingMapper = servicePricingMapper;
	}
	
	public List<OrderView> getAll(){
		return orderRepository.findAll().stream().map(order -> {return OrderView.builder().
				id(order.getId())
				.statusId(order.getOrderStatus().getId())
				.workerId(order.getWorker() == null ? -1L : order.getWorker().getId())
				.clientId(order.getClient().getId())
				.build();
		}).collect(Collectors.toList());
	}
	
	@Transactional
	public Long createOrder(OrderCreate orderCreate){
		Optional<Client> client = clientService.findById(orderCreate.getClientId());
		if(!client.isPresent()){
			throw new EntityNotFoundException("ClientNotFound");
		}else{
			Order order = new Order();
			order.setClient(client.get());
			order.setDateCreate(new Date());
			orderRepository.save(order);
			order.setOrderElement(orderElementService.createOrderElementByListServicePricingView(orderCreate.getSelectedServicePricingIds(), order));
			order.setOrderStatus(orderStatusService.findByName("Forming").get());
			orderRepository.save(order);
			return order.getId();
		}
	}
	
	@Transactional
	public OrderElementCreateList getElementsToCreateByOrderId(Long id){
		Optional<Order> order = orderRepository.findById(id);
		if(!order.isPresent() || order.get().getOrderStatus().getId() != 1){
			throw new EntityNotFoundException("OrderNotFound");
		}else{
			OrderElementCreateList orderElementCreateList = new OrderElementCreateList();
			orderElementCreateList.setOrderId(id);
			List<OrderElement> orderElements = order.get().getOrderElement();
			List<OrderElementCreate> orderElementCreates = new ArrayList<>();
			for(OrderElement orderElement : orderElements){
				OrderElementCreate orderElementCreate = new OrderElementCreate();
				orderElementCreate.setOrderElementId(orderElement.getId());
				orderElementCreate.setServicePricingView(servicePricingMapper.fromServicePricingToServicePricingView(orderElement.getServicePricing()));
				orderElementCreates.add(orderElementCreate);
			}
			orderElementCreateList.setOrderElementCreateList(orderElementCreates);
			return orderElementCreateList;
		}
	}
	
}
