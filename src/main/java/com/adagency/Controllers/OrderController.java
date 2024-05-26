package com.adagency.Controllers;

import com.adagency.dbwork.jparepo.CategoryRepository;
import com.adagency.dbwork.service.CategoryService;
import com.adagency.dbwork.service.OrderService;
import com.adagency.model.dto.order.OrderCreate;
import com.adagency.model.dto.order.OrderElementCreateList;
import com.adagency.model.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class OrderController {

	private final OrderService orderService;
	private final CategoryService categoryService;
	
	@Autowired
	public  OrderController(OrderService orderService, CategoryService categoryService){
		this.orderService = orderService;
		this.categoryService = categoryService;
	}
	
	@GetMapping("/orders")
	public String orders(Model model){
		model.addAttribute("orders",  orderService.getAll()); //todo pagination + find
		return "Order/orders";
	}
	
	@GetMapping("/orders/create/{id}")
	public String ordersCreate(@PathVariable Long id, Model model){
		OrderCreate orderCreate = new OrderCreate();
		orderCreate.setClientId(id);
		orderCreate.setClientSimpleCategoryList(categoryService.getCategoryToPreCreateOrder());
		model.addAttribute("category", orderCreate );
		return "Order/preCreate";
	}
	
	@PostMapping("/orders/create")
	public String ordersCreatePost(@ModelAttribute("category") OrderCreate orderCreate, Model model){
		Long orderId = orderService.createOrder(orderCreate);
		model.addAttribute("category", orderCreate );
		model.addAttribute("selectedIds", orderCreate.getSelectedServicePricingIds() );
		return "redirect:/orders/submitCreate/" + orderId;
	}
	
	@GetMapping("/orders/submitCreate/{id}")
	public String ordersSubmitCreate(@PathVariable Long id, Model model){
		model.addAttribute("elements", orderService.getElementsToCreateByOrderId(id));
		return "Order/submitCreate";
	}
	
	@PostMapping("/orders/submitCreate")
	public String ordersSubmitCreate(@ModelAttribute("elements")OrderElementCreateList orderElementCreateList, Model model){
		model.addAttribute("elements", orderElementCreateList);
		return "Order/submitCreate";
	}

}
