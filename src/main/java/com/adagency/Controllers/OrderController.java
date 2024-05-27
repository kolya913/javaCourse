package com.adagency.Controllers;

import com.adagency.dbwork.jparepo.CategoryRepository;
import com.adagency.dbwork.service.CategoryService;
import com.adagency.dbwork.service.ClientService;
import com.adagency.dbwork.service.OrderService;
import com.adagency.model.dto.order.OrderCreate;
import com.adagency.model.dto.order.OrderElementCreateList;
import com.adagency.model.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class OrderController {

	private final OrderService orderService;
	private final CategoryService categoryService;
	private final ClientService clientService;
	
	@Autowired
	public  OrderController(OrderService orderService, CategoryService categoryService, ClientService clientService){
		this.orderService = orderService;
		this.categoryService = categoryService;
		this.clientService = clientService;
	}
	
	@GetMapping("/orders")
	public String orders(Model model){
		model.addAttribute("orders",  orderService.getAll()); //todo pagination + find
		return "Order/orders";
	}
	
	@GetMapping("/orders/create/{id}")
	public String ordersCreate(@PathVariable Long id, Model model){
		if(orderService.checkExistOrder(id) || !clientService.findById(id).isPresent()){
			model.addAttribute("error", "Есть еще не оформленный заказ");
			model.addAttribute("orders",  orderService.getAll());
			return "Order/orders";
		}
		
		if(!clientService.findById(id).isPresent()){
			model.addAttribute("error", "Для создания нужен клиент");
			model.addAttribute("orders",  orderService.getAll());
			return "Order/orders";
		}
		
		OrderCreate orderCreate = new OrderCreate();
		orderCreate.setClientId(id);
		orderCreate.setClientSimpleCategoryList(categoryService.getCategoryToPreCreateOrder());
		model.addAttribute("category", orderCreate );
		return "Order/preCreate";
	}
	
	@PostMapping("/orders/create")
	public String ordersCreatePost(@ModelAttribute("category") OrderCreate orderCreate, Model model){
		try {
			Long orderId = orderService.createOrder(orderCreate);
			model.addAttribute("category", orderCreate );
			model.addAttribute("selectedIds", orderCreate.getSelectedServicePricingIds() );
			return "redirect:/orders/submitCreate/" + orderId;
		}catch (Exception e){
			model.addAttribute("error", e.getMessage());
			return "Order/preCreate";
		}
	}
	
	@GetMapping("/orders/submitCreate/{id}")
	public String ordersSubmitCreate(@PathVariable Long id, Model model){
		model.addAttribute("elements", orderService.getElementsToCreateByOrderId(id));
		return "Order/submitCreate";
	}
	
	@PostMapping("/orders/submitCreate")
	public String ordersSubmitCreate(@ModelAttribute("elements")OrderElementCreateList orderElementCreateList, Model model) throws Exception {
		model.addAttribute("elements", orderElementCreateList);
		orderService.createOrderElements(orderElementCreateList);
		return "redirect:/orders";
	}
	
	@GetMapping("/orders/details/{id}")
	public String orderDetails(@PathVariable Long id, Model model){
		
		return  "Order/orderDetails";
	}

	@PostMapping("/addtoorder")
	public String addToOrder(@RequestParam("pricingId") Long pricingId, @RequestParam("userId") Long userId) {
		try {
			orderService.addToOrder(pricingId,userId);
		}catch (Exception e){

		}
		return "Home/service";
	}


}
