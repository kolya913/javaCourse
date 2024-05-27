package com.adagency.dbwork.service;

import com.adagency.dbwork.jparepo.OrderStatusRepository;
import com.adagency.model.entity.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrderStatusService {
	
	private final OrderStatusRepository orderStatusRepository;
	
	@Autowired
	public OrderStatusService(OrderStatusRepository orderStatusRepository){
		this.orderStatusRepository = orderStatusRepository;
	}
	
	
	public Optional<OrderStatus> findByName(String name){
		return orderStatusRepository.findByName(name);
	}
	
	public Optional<OrderStatus> findById(Long id){
		return orderStatusRepository.findById(id);
	}
	
	public void createOrderStatus(OrderStatus orderStatus){
		orderStatusRepository.save(orderStatus);
	}
	
}
