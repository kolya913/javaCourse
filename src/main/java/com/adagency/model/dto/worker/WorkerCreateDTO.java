package com.adagency.model.dto.worker;

import com.adagency.model.entity.Position;
import com.adagency.model.entity.Role;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class WorkerCreateDTO {
	@NotEmpty
	private String name;
	@NotEmpty
	private String middleName;
	@NotEmpty
	private String lastName;
	@Email
	private String email;
	private String password;
	@NotEmpty
	private String phoneNumber;
	@NotEmpty
	private String inn;
	@NotEmpty
	private int salary;
	private Position position;
	private Role role;
	@NotEmpty
	private Long positionS;
	@NotEmpty
	private Long roleS;
}
