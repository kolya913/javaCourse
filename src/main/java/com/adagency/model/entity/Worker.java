package com.adagency.model.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "workers")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Worker extends BaseModelPerson {
	@Column(name = "inn")
	private String inn;

	@Column(name = "salary")
	private int salary;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "position_id")
	private Position position;
	
	@OneToMany(mappedBy = "worker", fetch = FetchType.LAZY)
	private List<Order> orders = new ArrayList<>();

}
