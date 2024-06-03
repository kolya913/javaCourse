package com.adagency.dbwork.service;

import com.adagency.dbwork.jparepo.OrderRepository;
import com.adagency.model.dto.order.OrderCreate;
import com.adagency.model.dto.order.OrderElementCreate;
import com.adagency.model.dto.order.OrderElementCreateList;
import com.adagency.model.dto.order.OrderView;
import com.adagency.model.dto.orderelement.OrderElementView;
import com.adagency.model.dto.person.UserProfileForm;
import com.adagency.model.dto.status.StatusView;
import com.adagency.model.entity.*;
import com.adagency.model.mapper.servicepricingmapper.ServicePricingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.*;
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
	private MediaFileService mediaFileService ;

	@Autowired
	private WorkerService workerService;
	
	
	@Autowired
	public OrderService(OrderRepository orderRepository, ClientService clientService,
	                    OrderStatusService orderStatusService, ServicePricingMapper servicePricingMapper) {
		this.orderRepository = orderRepository;
		this.clientService = clientService;
		this.orderStatusService = orderStatusService;
		this.servicePricingMapper = servicePricingMapper;
	}


	@Transactional
	public Optional<Order> getOrderById(Long id){
		return orderRepository.findById(id);
	}
	
	
	public Page<OrderView> getAll(Pageable pageable, Long workerId, Long clientId, Long orderId) {
		Page<Order> orders = orderRepository.findByCriteria(workerId, clientId, orderId, pageable);
		return orders.map(order -> OrderView.builder()
				.id(order.getId())
				.statusId(order.getOrderStatus().getId())
				.workerId(order.getWorker() == null ? -1L : order.getWorker().getId())
				.clientId(order.getClient().getId())
				.build());
	}

	
	@Transactional
	public Long createOrder(OrderCreate orderCreate) throws Exception {
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
	public boolean checkExistOrder(Long clientId){
		return  orderRepository.existsByClientIdAndOrderStatusId(clientId, 1L);
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
	
	@Transactional
	public void createOrderElements(OrderElementCreateList orderElementCreateList) throws Exception {
		Optional<Order> order = orderRepository.findById(orderElementCreateList.getOrderId());
		if(!order.isPresent()){
			throw new EntityNotFoundException("OrderNotFound");
		}else{
			orderElementService.createOrderElementByListAfterSubmit(orderElementCreateList.getOrderElementCreateList());
			order.get().setOrderStatus(orderStatusService.findByName("Created").get());
			orderRepository.save(order.get());
		}
	}

	@Transactional
	public void addToOrder(Long pricingId, Long userId) throws EntityNotFoundException{
		List<Order> orders = orderRepository.findByClientIdAndOrderStatusId(userId, 1L);
		Optional<Client> client = clientService.findById(userId);
		if(!client.isPresent()){
			throw new EntityNotFoundException("ClientNotFound");
		}
		if(orders.isEmpty()){
			Order order = new Order();
			order.setClient(client.get());
			order.setDateCreate(new Date());
			orderRepository.save(order);
			List<OrderElement> orderElements = new ArrayList<>();
			orderElements.add(orderElementService.addOrderElementToOrder(pricingId, order));
			order.setOrderElement(orderElements);
			order.setOrderStatus(orderStatusService.findByName("Forming").get());
			orderRepository.save(order);
		}else {
			Order order = orders.get(0);
			List<OrderElement> orderElements = order.getOrderElement();
			orderElements.add(orderElementService.addOrderElementToOrder(pricingId, order));
			order.setOrderElement(orderElements);
			orderRepository.save(order);
		}
	}
	
	@Transactional
	public OrderView getOrderViewForDetails(Long id){
		Optional<Order> order = orderRepository.findById(id);
		if(!order.isPresent()){
			throw new EntityNotFoundException("OrderNotFound");
		}else {
			OrderView orderView = new OrderView();
			orderView.setId(order.get().getId());
			orderView.setPayed(order.get().isPayed());
			if(order.get().getWorker()!=null){
				orderView.setWorker(UserProfileForm.builder()
						.id(order.get().getWorker().getId() == null ? -1 : order.get().getWorker().getId())
						.name(order.get().getWorker().getName() == null ? null : order.get().getWorker().getName())
						.lastName(order.get().getWorker().getLastName() == null ? null : order.get().getWorker().getLastName())
						.build());
				orderView.setWorkerId(order.get().getWorker().getId());
			}
			
			orderView.setClient(UserProfileForm.builder()
					.id(order.get().getClient().getId() == null ? -1 : order.get().getClient().getId())
					.name(order.get().getClient().getName() == null ? null : order.get().getClient().getName())
					.lastName(order.get().getClient().getLastName() == null ? null : order.get().getClient().getLastName())
					.build());
			orderView.setClientId(order.get().getClient().getId());
			orderView.setStatusView(StatusView.builder().id(order.get().getOrderStatus().getId()).name(order.get().getOrderStatus().getName()).build());
			List<OrderElementView> orderElementViewList = new ArrayList<>();
			if(order.get().getOrderElement() != null){
				for(OrderElement orderElement : order.get().getOrderElement()){
					OrderElementView orderElementView = new OrderElementView();
					orderElementView.setCount(orderElement.getCount());
					orderElementView.setText(orderElement.getText());
					orderElementView.setServiceId(orderElement.getServicePricing().getId());
					orderElementView.setServiceName(orderElement.getServicePricing().getServiceName());
					orderElementView.setMediaFileViews(orderElement.getMediaFiles().stream()
							.map(mediaFileService::getMediaFileView)
							.collect(Collectors.toList()));
					orderElementViewList.add(orderElementView);
				}
			}
			orderView.setOrderElementViewList(orderElementViewList);
			return orderView;
		}
	}

	@Transactional
	public void addWorkerToOrder(Long orderId, Long workerId){
		Optional<Order> order = orderRepository.findById(orderId);
		if(!order.isPresent()){
			throw new EntityNotFoundException("OrderNotFound");
		}
		if(workerId == -1){
			order.get().setWorker(null);
			order.get().setOrderStatus(orderStatusService.findByName("Created").get());
			
		}else{
			Optional<Worker> worker = workerService.findById(workerId);
			if(!worker.isPresent()){
				throw new EntityNotFoundException("WorkerNotFound");
			}
			order.get().setWorker(worker.get());
			order.get().setOrderStatus(orderStatusService.findByName("Active").get());
		}
		orderRepository.save(order.get());
	}


	@Transactional
	public void updateOrderCheck(Long orderId, String status){
		Optional<Order> order = orderRepository.findById(orderId);
		if(!order.isPresent()){
			throw new EntityNotFoundException("OrderNotFound");
		}else{

			if(order.get().getOrderStatus().getId() <= 2 && status.equals("no")){
				order.get().setOrderStatus(orderStatusService.findByName("Close").get());
			}
			
			if(order.get().getOrderStatus().getId() == 3){
				order.get().setOrderStatus(orderStatusService.findByName("Check").get());
			}
			
			if(order.get().getOrderStatus().getId() == 6){
				order.get().setOrderStatus(orderStatusService.findByName("Final").get());
			}

			if(status != null && !status.isEmpty()){
				if(status.equals("no") && order.get().getOrderStatus().getId() == 4 ){
					order.get().setOrderStatus(orderStatusService.findByName("Active").get());
				} else if (status.equals("no") && order.get().getOrderStatus().getId() == 7 ) {
					order.get().setOrderStatus(orderStatusService.findByName("Payed").get());
				} else if (status.equals("no") && order.get().getOrderStatus().getId() == 8 ) {
					order.get().setOrderStatus(orderStatusService.findByName("Close").get());
				}
				
				if(status.equals("yes") && order.get().getOrderStatus().getId() == 4){
					order.get().setOrderStatus(orderStatusService.findByName("Checked").get());
				} else if (status.equals("yes") && order.get().getOrderStatus().getId() == 8 ) {
					order.get().setOrderStatus(orderStatusService.findByName("Finish").get());
				}

			}


			orderRepository.save(order.get());
		}
	}

	@Transactional
	public void pay(Long orderId, Long clientId) throws Exception {
		Optional<Order> order = orderRepository.findById(orderId);
		if(!order.isPresent()){
			throw new EntityNotFoundException("OrderNotFound");
		}else{
			if(!Objects.equals(order.get().getClient().getId(), clientId)){
				throw new EntityNotFoundException();
			}
			if(order.get().getOrderStatus().getId() == 5){
				order.get().setOrderStatus(orderStatusService.findByName("Payed").get());
				order.get().setPayed(true);
				orderRepository.save(order.get());
			}
		}
	}

}
