package com.adagency.model.mapper.service;

import com.adagency.model.dto.service.ServiceCreate;
import com.adagency.model.entity.Service;
import org.springframework.stereotype.Component;

@Component
public class ServiceMapper {
    public Service fromServiceCreateToService(ServiceCreate serviceCreate){
        return Service.builder()
                .name(serviceCreate.getName())
                .description(serviceCreate.getDescription())
                .
                .build();
    }
}
