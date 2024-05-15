package com.adagency.model.dto.worker;

import com.adagency.model.entity.Position;
import com.adagency.model.entity.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkerCreateDTO {
	private String name;
	private String middleName;
	private String lastName;
	private String email;
	private String password;
	private String phoneNumber;
	private String inn;
	private int salary;
	private Position position;
	private Role role;
	private Long positionS;
	private Long roleS;
}
//todo valid + empty