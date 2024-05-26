package com.adagency.model.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "orders")
public class Order {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;
	
	private Date dateCreate;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "worker_id")
	private Worker worker;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "client_id")
	private Client client;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_status_id")
	private OrderStatus orderStatus;
	
	@OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
	private List<OrderElement> orderElement;
}
