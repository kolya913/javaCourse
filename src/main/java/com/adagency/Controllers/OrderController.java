package com.adagency.Controllers;

import com.adagency.dbwork.jparepo.CategoryRepository;
import com.adagency.dbwork.service.CategoryService;
import com.adagency.dbwork.service.ClientService;
import com.adagency.dbwork.service.OrderService;
import com.adagency.dbwork.service.WorkerService;
import com.adagency.model.dto.order.OrderCreate;
import com.adagency.model.dto.order.OrderElementCreateList;
import com.adagency.model.entity.Category;
import com.adagency.model.entity.Worker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class OrderController {

	private final OrderService orderService;
	private final CategoryService categoryService;
	private final ClientService clientService;

	@Autowired
	private WorkerService workerService;
	
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
		model.addAttribute("order", orderService.getOrderViewForDetails(id));
		return  "Order/orderDetails";
	}
	
	@PostMapping("/addtoorder")
	@ResponseBody
	public ResponseEntity<?> addToOrder(@RequestParam("pricingId") Long pricingId, @RequestParam("userId") Long userId) {
		if (userId == -1) {
			return ResponseEntity
					.status(HttpStatus.UNAUTHORIZED)
					.body("Необходимо войти в аккаунт или зарегистрироваться, чтобы добавить услугу в заказ.");
		}
		try {
			orderService.addToOrder(pricingId, userId);
			return ResponseEntity.ok("Услуга успешно добавлена в заказ.");
		} catch (Exception e) {
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Произошла ошибка при добавлении услуги в заказ.");
		}
	}

	@PostMapping("/orders/addWorker")
	@ResponseBody
	public ResponseEntity<?> addWorker(@RequestParam("workerId") Long workerId, @RequestParam("orderId") Long orderId){
		try {
			orderService.addWorkerToOrder(orderId, workerId);
			return ResponseEntity.ok("Работник успешно добавлена в заказ.");
		}catch (Exception e){
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Произошла ошибка при добавлении работника в заказ.");
		}
	}

	@PostMapping("/orders/sendFiles")
	@ResponseBody
	public ResponseEntity<?> sendFiles(@RequestParam("orderId") Long orderId){
		try {
			orderService.updateOrderCheck(orderId, "");
			return ResponseEntity.ok("Файлы отправлены");
		}catch (Exception e){
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Ошибка отправки");
		}
	}

	@PostMapping("/orders/agree")
	@ResponseBody
	public ResponseEntity<?> orderAgree(@RequestParam("orderId") Long orderId, @RequestParam("s") String s){
		try {
			orderService.updateOrderCheck(orderId, s);
			if(s.equals("no") || s.equals("yes")){
				return ResponseEntity.ok("Принято");
			}
			return ResponseEntity.ok("Файлы отправлены");
		}catch (Exception e){
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Ошибка отправки");
		}
	}

	@PostMapping("/orders/pay")
	@ResponseBody
	public ResponseEntity<?> orderPay(@RequestParam("orderId") Long orderId){
		try {
			orderService.pay(orderId);

			return ResponseEntity.ok("Оплачено");
		}catch (Exception e){
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Ошибка отправки");
		}
	}


}
