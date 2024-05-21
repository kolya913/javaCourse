package com.adagency.model.mapper.service;

import com.adagency.model.dto.service.ServiceCreate;
import com.adagency.model.dto.service.ServiceEdit;
import com.adagency.model.dto.service.ServiceView;
import com.adagency.model.entity.Service;
import org.springframework.stereotype.Component;

@Component
public class ServiceMapper {
    public Service fromServiceCreateToService(ServiceCreate serviceCreate){
        return Service.builder()
                .name(serviceCreate.getName())
                .shortDescription(serviceCreate.getShortDescription())
                .description(serviceCreate.getDescription())
                .build();
    }
    
    public ServiceView fromServiceToServiceView(Service service){
        return ServiceView.builder()
                .id(service.getId())
                .name(service.getName())
                .description(service.getDescription())
                .categoryId(service.getCategory().getId())
                .build();
    }
    
    public ServiceView fromServiceToServiceViewFull(Service service){
        return ServiceView.builder()
                .id(service.getId())
                .name(service.getName())
                .description(service.getDescription())
                .categoryId(service.getCategory().getId())
                .build();
    }
    
    
    public ServiceEdit fromServiceToServiceEdit(Service service){
        return ServiceEdit.builder()
                .id(service.getId())
                .name(service.getName())
                .deleteFlag(service.isDeleteFlag())
                .statusId(service.getStatus().getId())
                .shortDescription(service.getShortDescription())
                .description(service.getDescription())
                .categoryId(service.getCategory().getId())
                .build();
    }


    public Service updateServiceFromServiceEdit(Service service, ServiceEdit serviceEdit){
        service.setName(serviceEdit.getName());
        service.setShortDescription(serviceEdit.getShortDescription());
        service.setDescription(serviceEdit.getDescription());
        service.setDeleteFlag(serviceEdit.isDeleteFlag());
        return service;
    }
    
    
    
}
