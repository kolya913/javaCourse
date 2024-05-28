package com.adagency.model.dto.person;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class LoginModel {
	@Email
	private String email;
	//@Pattern(regexp = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[a-z]).{6,20}$",
			//message = "Пароль должен быть длиной от 6 до 20 символов и содержать как минимум одну заглавную латинскую букву и одну цифру.")
	private String password;
}
