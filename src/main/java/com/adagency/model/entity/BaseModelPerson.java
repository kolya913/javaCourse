package com.adagency.model.entity;


import lombok.Data;
import lombok.experimental.SuperBuilder;
import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.Date;

@Entity
@Data
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "base_model_person")
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@SuperBuilder
public class BaseModelPerson{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@Column(name = "name")
	private String name;

	@Column(name = "middleName")
	private String middleName;

	@Column(name = "lastName")
	private String lastName;

	@Column(name = "email")
	@Email
	private String email;

	@Column(name = "password")
	private String password;

	@Column(name = "phoneNumber")
	private String phoneNumber;

	@Column(name = "dateCreate")
	private Date dateCreate;

	@ManyToOne
	@JoinColumn(name = "role_id")
	private Role role;

	@Column(name = "deleteFlag")
	private boolean deleteFlag;

	@Column(name = "type", insertable = false, updatable = false)
	private String type;

	public BaseModelPerson() {}

	public BaseModelPerson(String name, String middleName, String lastName, String email, String password, String phoneNumber, Date dateCreate, Role role, boolean deleteFlag) {
		this.name = name;
		this.middleName = middleName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.phoneNumber = phoneNumber;
		this.dateCreate = dateCreate;
		this.role = role;
		this.deleteFlag = deleteFlag;
	}


}