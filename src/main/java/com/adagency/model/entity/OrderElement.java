package com.adagency.model.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderElement {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	private Order order;
	
	private String text;
	
	private int count = 0;
	
	@OneToMany(mappedBy = "orderElement", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<MediaFile> mediaFiles;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "service_pricing_id")
	private ServicePricing servicePricing;
	
}
