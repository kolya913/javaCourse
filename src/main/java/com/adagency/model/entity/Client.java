package com.adagency.model.entity;

import lombok.experimental.SuperBuilder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Entity
@Table(name = "clients")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Client extends BaseModelPerson {
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "company_id")
	private Company company;
}
