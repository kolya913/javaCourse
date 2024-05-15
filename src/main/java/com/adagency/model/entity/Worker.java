package com.adagency.model.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import javax.persistence.*;


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

	@ManyToOne
	@JoinColumn(name = "position_id")
	private Position position;

}
