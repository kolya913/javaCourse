package com.adagency.dbwork.service;

import com.adagency.config.MvcConfig;
import com.adagency.dbwork.jparepo.ServiceRepository;
import com.adagency.model.dto.mediafile.MediaFileCreate;
import com.adagency.model.dto.mediafile.MediaFileView;
import com.adagency.model.dto.service.ServiceCreate;
import com.adagency.model.dto.service.ServiceEdit;
import com.adagency.model.dto.service.ServiceView;
import com.adagency.model.dto.servicepricing.ServicePricingEdit;
import com.adagency.model.dto.servicepricing.ServicePricingView;
import com.adagency.model.entity.MediaFile;
import com.adagency.model.entity.Status;
import com.adagency.model.mapper.service.ServiceMapper;
import com.adagency.model.mapper.servicepricingmapper.ServicePricingMapper;
import com.adagency.model.mapper.status.StatusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import com.adagency.model.entity.Service ;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final MediaFileService mediaFileService;
    private final ServiceMapper serviceMapper;
    private final CategoryService categoryService;
    private final StatusService statusService;
    private final StatusMapper statusMapper;
    private final ServicePricingMapper servicePricingMapper;

    @Autowired
    public ServiceService(ServiceRepository serviceRepository, MediaFileService mediaFileService,
                          ServiceMapper serviceMapper, CategoryService categoryService,
                          StatusService statusService, StatusMapper statusMapper, ServicePricingMapper servicePricingMapper){
        this.serviceRepository = serviceRepository;
        this.mediaFileService = mediaFileService;
        this.serviceMapper = serviceMapper;
        this.categoryService = categoryService;
        this.statusService = statusService;
        this.statusMapper = statusMapper;
        this.servicePricingMapper = servicePricingMapper;
    }

    @Transactional
    public void CreateService(ServiceCreate serviceCreate) throws IOException, Exception {
        Service service = serviceMapper.fromServiceCreateToService(serviceCreate);
        service.setStatus(statusService.findByName("Awaiting approval").get());
        service.setCategory(categoryService.getCategoryById(serviceCreate.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("CategoryNotFound")));
        service.setDeleteFlag(false);
        serviceRepository.save(service);
        List<MediaFile> mediaFiles = new ArrayList<>();
        for (MediaFileCreate mediaFileCreate : serviceCreate.getFiles()){
            mediaFiles.add(mediaFileService.testCreateWithTransferFileToPathServer(
                    mediaFileCreate,
                    "ServiceMedia",
                    mediaFileCreate.getDescription(),
                    MvcConfig.RESOURCE_PATH + "images/Service/" + service.getId()
                            + "/" + mediaFileCreate.getFile().getOriginalFilename(),
                    mediaFileCreate.getAlt()
            ));
        }
        service.setMediaFiles(mediaFiles);
        serviceRepository.save(service);
    }
    
    @Transactional
    public ServiceView getServiceView(Long id){
        Optional<Service> service = serviceRepository.findById(id);
        
        if(service.isPresent()){
            ServiceView serviceView = serviceMapper.fromServiceToServiceViewFull(service.get());
            serviceView.setMedia(service.get().getMediaFiles().stream().parallel()
                            .filter(mediaFile -> !mediaFile.isMain())
                    .map(mediaFileService::getMediaFileView)
                    .collect(Collectors.toList())
            );
            serviceView.setStatus(statusMapper.fromStatusToStatusView(service.get().getStatus()));
            serviceView.setPricingViewList(service.get().getServicePricings().stream()
                    .filter(servicePricing -> "Active".equals(servicePricing.getStatus().getName()))
                    .map(servicePricingMapper::fromServicePricingToServicePricingView)
                    .collect(Collectors.toList()));
            return serviceView;
        }
        throw new EntityNotFoundException("ServiceNotFound");
    }
    
    
    @Transactional
    public ServiceEdit getServiceEdit(Long id){
        Optional<Service> service = serviceRepository.findById(id);
        if(service.isPresent()){
            ServiceEdit serviceEdit = serviceMapper.fromServiceToServiceEdit(service.get());
            serviceEdit.setMediaFiles(service.get().getMediaFiles().stream().parallel()
                    .map(mediaFileService::getMediaFileView)
                    .collect(Collectors.toList()));
            serviceEdit.setStatus(statusMapper.fromStatusToStatusView(service.get().getStatus()));
            return serviceEdit;
        }
        throw new EntityNotFoundException("ServiceNotFound");
    }
    

    @Transactional
    public void updateService(ServiceEdit serviceEdit) throws IOException, Exception {
        Optional<Service> service = serviceRepository.findById(serviceEdit.getId());
        if(!service.isPresent()){
            throw new EntityNotFoundException("ServiceNotFound");
        }
        if(service.get().getStatus().getId() != serviceEdit.getStatusId()){
            Optional<Status> status = statusService.findById(serviceEdit.getStatusId());
            if(!status.isPresent()){
                throw new EntityNotFoundException("StatusNotFound");
            }else{
                service.get().setStatus(status.get());
            }
        }
        serviceMapper.updateServiceFromServiceEdit(service.get(), serviceEdit);
        for(MediaFileView mediaFile : serviceEdit.getMediaFiles()){
            mediaFileService.updateMedFileV(mediaFile);
        }
        
        for(MediaFileCreate mediaFile : serviceEdit.getMediaFileCreates()){
            service.get().getMediaFiles().add(mediaFileService.testCreateWithTransferFileToPathServer(
                    mediaFile,
                    "ServiceMedia",
                    mediaFile.getDescription(),
                    MvcConfig.RESOURCE_PATH + "images/Service/" + service.get().getId()
                            + "/" + mediaFile.getFile().getOriginalFilename(),
                    mediaFile.getAlt()
            ));
        }
        serviceRepository.save(service.get());
    }
    
    
    @Transactional
    public void removeService(Long id){
        Optional<Service> service = serviceRepository.findById(id);
        if(service.isPresent()){
          service.get().setDeleteFlag(true);//todo флаг удаления для позиций
        }
        throw new EntityNotFoundException("ServiceNotFound");
    }


    public boolean checkExsist(Long id){
        Optional<Service> service = serviceRepository.findById(id);
        if(service.isPresent()){
            return true;
        }else {
            return false;
        }
    }
    
    public Optional<Service> getServiceById(Long id){
        return serviceRepository.findById(id);
    }
    
    @Transactional
    public List<ServicePricingEdit> getServicesPricingsToEdit(Long id){
        Optional<Service> service = serviceRepository.findById(id);
        if(!service.isPresent()){
            throw new EntityNotFoundException("ServiceNotFound");
        }else{
            return service.get().getServicePricings().stream().parallel().map(servicePricing -> {
            ServicePricingEdit servicePricingEdit = servicePricingMapper.fromServicePricingToServicePricingEdit(servicePricing);
            servicePricingEdit.setStatusView(statusMapper.fromStatusToStatusView(servicePricing.getStatus()));
            return servicePricingEdit;
            }).collect(Collectors.toList());
        }
    }

}
