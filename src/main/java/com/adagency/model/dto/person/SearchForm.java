package com.adagency.model.dto.person;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchForm {
    private String email;
    private String name;
    private String middleName;
    private String lastName;
    private String phoneNumber;
    private String inn;
    private boolean client;
    private boolean worker;
    private boolean deleteFlag;
}
