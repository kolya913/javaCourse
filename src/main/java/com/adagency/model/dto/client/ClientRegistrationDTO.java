package com.adagency.model.dto.client;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClientRegistrationDTO {
    private String name;
    private String middleName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
}

