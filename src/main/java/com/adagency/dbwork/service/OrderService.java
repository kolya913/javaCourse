package com.adagency.dbwork.service;

import com.adagency.dbwork.jparepo.OrderRepository;
import com.adagency.model.dto.category.ClientSimpleCategory;
import com.adagency.model.dto.mediafile.MediaFileView;
import com.adagency.model.dto.order.OrderCreate;
import com.adagency.model.dto.order.OrderElementCreate;
import com.adagency.model.dto.order.OrderElementCreateList;
import com.adagency.model.dto.order.OrderView;
import com.adagency.model.dto.orderelement.OrderElementView;
import com.adagency.model.dto.person.UserProfileForm;
import com.adagency.model.dto.service.ServiceSimpleView;
import com.adagency.model.dto.servicepricing.ServicePricingView;
import com.adagency.model.dto.status.StatusView;
import com.adagency.model.entity.*;
import com.adagency.model.mapper.servicepricingmapper.ServicePricingMapper;
import com.sun.org.apache.xpath.internal.operations.Or;
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
	private MediaFileService mediaFileService ;
	
	
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
	public Long createOrder(OrderCreate orderCreate) throws Exception {
		Optional<Client> client = clientService.findById(orderCreate.getClientId());
		if(!client.isPresent()){
			throw new EntityNotFoundException("ClientNotFound");
		}else{
			//long existingOrders = orderRepository.countOrdersByClientIdAndOrderStatusId(orderCreate.getClientId());
			
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
					/*List<MediaFileView> mediaFileViews = new ArrayList<>();
					for(MediaFile mediaFile : orderElement.getMediaFiles()){
						mediaFileViews.add(mediaFileService.getMediaFileView(mediaFile));
					}*/
					orderElementView.setMediaFileViews(orderElement.getMediaFiles().stream()
							.map(mediaFileService::getMediaFileView)
							.collect(Collectors.toList()));
					//orderElementView.setMediaFileViews(mediaFileViews);
					orderElementViewList.add(orderElementView);
				}
			}
			orderView.setOrderElementViewList(orderElementViewList);
			return orderView;
		}
	}

}
