package com.adagency.dbwork.service;

import com.adagency.dbwork.jparepo.OrderRepository;
import com.adagency.model.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
	private final OrderRepository orderRepository;
	
	@Autowired
	public OrderService(OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}
	
	public List<Order> getAll(){
		return orderRepository.findAll();
	}
	
}
