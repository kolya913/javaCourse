package com.adagency.model.dto.person;

import com.adagency.model.dto.position.Position;
import com.adagency.model.entity.Company;
import com.adagency.model.entity.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@Builder
public class UserProfileForm {
    private Long id;
    @NotEmpty(message = "Имя не должно быть путым")
    private String name;
   
    private String middleName;
    @NotEmpty(message = "Фамилия не должна быть пустой")
    private String lastName;
    @Email(message = "Неправильный ввод почты")
    @NotEmpty(message = "Почта не может быть пустой")
    private String email;
    @NotEmpty(message = "Телефон не может быть пустым")
    private String phoneNumber;
    @Nullable
    private String type;
    @Nullable
    private String password;
    @Nullable
    private Integer salary;
    @Nullable
    private String inn;
    @Nullable
    private Position position;
    @Nullable
    private Company company;
    @Nullable
    private Role role;
    @Nullable
    private Long roleS;
    @Nullable
    private Long positionS;
}

