package com.adagency.model.mapper.basemodelperson;

import com.adagency.model.dto.security.AuthorizePersonDTO;
import com.adagency.model.entity.BaseModelPerson;
import org.springframework.stereotype.Component;

@Component
public class BaseModelPersonMapperImpl {
    public AuthorizePersonDTO fromBMPtoAuthorize(BaseModelPerson baseModelPerson) {
        if (baseModelPerson == null) {
            return null;
        }
        return AuthorizePersonDTO.builder()
                .id(baseModelPerson.getId())
                .email(baseModelPerson.getEmail())
                .password(baseModelPerson.getPassword())
                .role(baseModelPerson.getRole())
                .build();
    }
}
