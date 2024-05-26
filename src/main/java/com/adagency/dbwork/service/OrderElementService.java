package com.adagency.dbwork.service;

import com.adagency.config.MvcConfig;
import com.adagency.dbwork.jparepo.OrderElementRepository;
import com.adagency.model.dto.mediafile.MediaFileCreate;
import com.adagency.model.dto.order.OrderElementCreate;
import com.adagency.model.dto.servicepricing.ServicePricingView;
import com.adagency.model.entity.MediaFile;
import com.adagency.model.entity.Order;
import com.adagency.model.entity.OrderElement;
import com.adagency.model.entity.ServicePricing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderElementService {
	private final OrderElementRepository orderElementRepository;
	private final ServicePricingService servicePricingService;
	private final MediaFileService mediaFileService;
	
	@Autowired
	public OrderElementService(OrderElementRepository orderElementRepository, ServicePricingService servicePricingService,
	                           MediaFileService mediaFileService){
		this.orderElementRepository = orderElementRepository;
		this.servicePricingService = servicePricingService;
		this.mediaFileService = mediaFileService;
	}
	
	
	@Transactional
	public List<OrderElement> createOrderElementByListServicePricingView(List<Long> servicePricingViewList, Order order) {
		return servicePricingViewList.stream()
				.map(servicePricingView -> {
					OrderElement orderElement = new OrderElement();
					orderElement.setOrder(order);
					orderElement.setServicePricing(servicePricingService.getServicePricingById(servicePricingView).orElseThrow());
					return orderElementRepository.save(orderElement);
				})
				.collect(Collectors.toList());
	}
	
	@Transactional
	public void createOrderElementByListAfterSubmit(List<OrderElementCreate> orderElementCreates) throws Exception {
		for (OrderElementCreate orderElementCreate : orderElementCreates) {
			Optional<OrderElement> orderElement = orderElementRepository.findById(orderElementCreate.getOrderElementId());
			if (!orderElement.isPresent()) {
				throw new EntityNotFoundException("OrderElementNotFound");
			} else {
				orderElement.get().setCount(orderElementCreate.getCount());
				orderElement.get().setText(orderElementCreate.getText());
				List<MediaFile> mediaFiles = new ArrayList<>();
				if (orderElementCreate.getFile() != null) {
					for (MediaFileCreate mediaFileCreate : orderElementCreate.getFile()) {
						if (mediaFileCreate.getFile() != null && !mediaFileCreate.getFile().isEmpty()) {
							MediaFile mediaFile = (mediaFileService.testCreateWithTransferFileToPathServer(
									mediaFileCreate,
									"OrderElement",
									mediaFileCreate.getDescription(),
									MvcConfig.RESOURCE_PATH + "images/OrderElement/" + orderElement.get().getId()
											+ "/" + mediaFileCreate.getFile().getOriginalFilename(),
									mediaFileCreate.getAlt()
							));
							mediaFile.setOrderElement(orderElement.get());
							mediaFileService.save(mediaFile);
						}
					}
				}
				orderElement.get().setMediaFiles(mediaFiles);
				orderElementRepository.save(orderElement.get());
			}
		}
	}
	
	
	
	
}
