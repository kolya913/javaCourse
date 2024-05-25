package com.adagency.Controllers;

import com.adagency.dbwork.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class OrderController {

	private final OrderService orderService;
	
	@Autowired
	public  OrderController(OrderService orderService){
		this.orderService = orderService;
	}
	
	@GetMapping("/orders")
	public String orders(Model model){
		model.addAttribute("orders",  orderService.getAll()); //todo pagination + find
		return "Order/orders.html";
	}
	
	@GetMapping("/orders/create/{id}")
	public String ordersCreate(@PathVariable Long id, Model model){
		model.addAttribute("orders",  orderService.getAll()); //todo pagination + find
		return "Order/orders";
	}

}
