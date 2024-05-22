package com.adagency.model.entity;

import lombok.*;

import javax.persistence.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicePricing {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	private String serviceName;
	
	private float price;
	
	private int minPeriodInDays;
	
	private int maxPeriodInDays;
	
	private int circulation;
	
	private String advertisementType;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "service_id")
	private Service service;
	
	
}

