package com.adagency.Controllers;

import com.adagency.dbwork.jparepo.CategoryRepository;
import com.adagency.dbwork.service.*;
import com.adagency.model.dto.order.OrderCreate;
import com.adagency.model.dto.order.OrderElementCreateList;
import com.adagency.model.dto.order.SearchFormForOrder;
import com.adagency.model.entity.BaseModelPerson;
import com.adagency.model.entity.Category;
import com.adagency.model.entity.Order;
import com.adagency.model.entity.Worker;
import com.adagency.model.security.CustomUserDetails;
import com.sun.org.apache.xpath.internal.operations.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Objects;
import java.util.Optional;

@Controller
public class OrderController {

	private final OrderService orderService;
	private final CategoryService categoryService;
	private final ClientService clientService;

	@Autowired
	private WorkerService workerService;

	@Autowired
	private BaseModelPersonService baseModelPersonService;
	
	@Autowired
	public  OrderController(OrderService orderService, CategoryService categoryService, ClientService clientService){
		this.orderService = orderService;
		this.categoryService = categoryService;
		this.clientService = clientService;
	}
	
	@GetMapping("/orders")
	public String orders(Model model, Authentication authentication,
	                     @RequestParam(value = "page", required = false, defaultValue = "1") int currentPage,
	                     @RequestParam(value = "size", required = false, defaultValue = "15") int pageSize) {
		CustomUserDetails currentUser = (CustomUserDetails) authentication.getPrincipal();
		Long currentUserId = currentUser.getUserId();
		String role = currentUser.getAuthorities().iterator().next().getAuthority();
		Pageable pageable = PageRequest.of(currentPage - 1, pageSize);
		
		model.addAttribute("orderSearch",new SearchFormForOrder());
		
		Optional<BaseModelPerson> person = baseModelPersonService.findById(currentUserId);
		person.ifPresent(baseModelPerson -> {
			if(baseModelPerson.getType().equals("Client")){
				model.addAttribute("orders", orderService.getAll(pageable, null,  baseModelPerson.getId(), null));
			} else if(baseModelPerson.getType().equals("Worker") && role.equals("ROLE_AGENT")){
				model.addAttribute("orders", orderService.getAll(pageable,  baseModelPerson.getId(), null, null));
			}else {
				model.addAttribute("orders", orderService.getAll(pageable, null, null, null));
			}
		});
		
		return "Order/orders";
	}
	
	
	
	@PostMapping("/search")
	public String handleSearchForm(
			@ModelAttribute("orderSearch") SearchFormForOrder searchForm,
			@RequestParam(value = "size", required = false, defaultValue = "15") int size,
			@RequestParam(value = "page", required = false, defaultValue = "1") int page,
			Model model, Authentication authentication) {
		
		CustomUserDetails currentUser = (CustomUserDetails) authentication.getPrincipal();
		Long currentUserId = currentUser.getUserId();
		String role = currentUser.getAuthorities().iterator().next().getAuthority();
		Pageable pageable = PageRequest.of(page - 1, size);
		
		model.addAttribute("size", size);
		model.addAttribute("page", page);
		
		Optional<BaseModelPerson> person = baseModelPersonService.findById(currentUserId);
		person.ifPresent(baseModelPerson -> {
			if (baseModelPerson.getType().equals("Client")) {
				model.addAttribute("orders", orderService.getAll(pageable, searchForm.getWorkerId(), baseModelPerson.getId(), searchForm.getOrderId()));
			} else if (baseModelPerson.getType().equals("Worker") && role.equals("ROLE_AGENT")) {
				model.addAttribute("orders", orderService.getAll(pageable, baseModelPerson.getId(), searchForm.getClientId(), searchForm.getOrderId()));
			} else {
				model.addAttribute("orders", orderService.getAll(pageable, searchForm.getWorkerId(), searchForm.getClientId(), searchForm.getOrderId()));
			}
		});
		
		return "Order/orders";
	}
	
	
	
	
	@GetMapping("/orders/create/{id}")
	public String ordersCreate(@PathVariable Long id, Model model){
		if(orderService.checkExistOrder(id) || !clientService.findById(id).isPresent()){
			model.addAttribute("error", "Есть еще не оформленный заказ");
			return "Order/orders";
		}
		
		if(!clientService.findById(id).isPresent()){
			model.addAttribute("error", "Для создания нужен клиент");
			//model.addAttribute("orders",  orderService.getAll());
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
	public String ordersSubmitCreate(@PathVariable Long id, Model model, Authentication authentication) {
		CustomUserDetails currentUser = (CustomUserDetails) authentication.getPrincipal();
		Long currentUserId = currentUser.getUserId();
		String role = currentUser.getAuthorities().iterator().next().getAuthority();
		Optional<Order> order = orderService.getOrderById(id);
		if(!order.isPresent()){
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "OrderNotFound");
		}
		if(role.equals("ROLE_CLIENT") && !Objects.equals(currentUserId, order.get().getClient().getId())){
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "OrderNetyDostupa");
		}
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
	public ResponseEntity<?> addToOrder(@RequestParam("pricingId") Long pricingId) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return ResponseEntity
					.status(HttpStatus.UNAUTHORIZED)
					.body("Необходимо войти в аккаунт или зарегистрироваться, чтобы добавить услугу в заказ.");
		}

		CustomUserDetails currentUser = (CustomUserDetails) authentication.getPrincipal();
		Long currentUserId = currentUser.getUserId();
		String role = currentUser.getAuthorities().iterator().next().getAuthority();
		if (currentUserId == null || currentUserId == -1) {
			return ResponseEntity
					.status(HttpStatus.UNAUTHORIZED)
					.body("Необходимо войти в аккаунт или зарегистрироваться, чтобы добавить услугу в заказ.");
		}

		if(role.equals("ROLE_CLIENT")){
			try {
				orderService.addToOrder(pricingId, currentUserId);
				return ResponseEntity.ok("Услуга успешно добавлена в заказ.");
			} catch (Exception e) {
				return ResponseEntity
						.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("ExpeПроизошла ошибка при добавлении услуги в заказ. role= " + role);
			}
		}else{
			return ResponseEntity
					.status(HttpStatus.FORBIDDEN)
					.body("Произошла ошибка при добавлении услуги в заказ.");
		}

    }


	@PostMapping("/orders/addWorker")
	@ResponseBody
	public ResponseEntity<?> addWorker(@RequestParam("workerId") Long workerId, @RequestParam("orderId") Long orderId){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return ResponseEntity
					.status(HttpStatus.UNAUTHORIZED)
					.body("Необходимо войти в аккаунт или зарегистрироваться, чтобы добавить услугу в заказ.");
		}
		
		CustomUserDetails currentUser = (CustomUserDetails) authentication.getPrincipal();
		Long currentUserId = currentUser.getUserId();
		String role = currentUser.getAuthorities().iterator().next().getAuthority();
		if(!(role.equals("ROLE_AGENT") && !Objects.equals(currentUserId, workerId))){
			return ResponseEntity
					.status(HttpStatus.FORBIDDEN)
					.body("Нету прав");
		}
		
		try {
			orderService.addWorkerToOrder(orderId, workerId);
			return ResponseEntity.ok("Работник успешно добавлен в заказ.");
		}catch (Exception e){
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Произошла ошибка при добавлении работника в заказ.");
		}
	}
	
	@PostMapping("/orders/sendFiles")
	@ResponseBody
	public ResponseEntity<?> sendFiles(@RequestParam("orderId") Long orderId){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails currentUser = (CustomUserDetails) authentication.getPrincipal();
		String role = currentUser.getAuthorities().iterator().next().getAuthority();
		if(!(role.equals("ROLE_AGENT"))){
			return ResponseEntity
					.status(HttpStatus.FORBIDDEN)
					.body("Нету прав");
		}
		try {
			orderService.updateOrderCheck(orderId, "Check");
			return ResponseEntity.ok("Файлы отправлены");
		} catch (Exception e) {
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
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails currentUser = (CustomUserDetails) authentication.getPrincipal();
		String role = currentUser.getAuthorities().iterator().next().getAuthority();
		if(!(role.equals("ROLE_CLIENT"))){
			return ResponseEntity
					.status(HttpStatus.FORBIDDEN)
					.body("Нету прав");
		}
		Long currentUserId = currentUser.getUserId();
		try {
			orderService.pay(orderId, currentUserId);

			return ResponseEntity.ok("Оплачено");
		}catch (EntityNotFoundException e){
			return ResponseEntity
					.status(HttpStatus.FORBIDDEN)
					.body("Ошибка доступа");
		}catch (Exception e){
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Ошибка отправки");
		}
	}


}
