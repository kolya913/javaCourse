package com.adagency.dbwork.service;

import com.adagency.config.MvcConfig;
import com.adagency.dbwork.jparepo.ServiceRepository;
import com.adagency.model.dto.mediafile.MediaFileCreate;
import com.adagency.model.dto.service.ServiceCreate;
import com.adagency.model.entity.Category;
import com.adagency.model.entity.MediaFile;
import com.adagency.model.mapper.service.ServiceMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import com.adagency.model.entity.Service ;
import org.springframework.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final MediaFileService mediaFileService;
    private final ServiceMapper serviceMapper;
    private final CategoryService categoryService;

    @Autowired
    public ServiceService(ServiceRepository serviceRepository, MediaFileService mediaFileService,
                          ServiceMapper serviceMapper, CategoryService categoryService){
        this.serviceRepository = serviceRepository;
        this.mediaFileService = mediaFileService;
        this.serviceMapper = serviceMapper;
        this.categoryService = categoryService;
    }

    @Transactional
    public void CreateService(ServiceCreate serviceCreate) throws IOException, Exception {
        Service service = serviceMapper.fromServiceCreateToService(serviceCreate);
        service.setCategory(categoryService.getCategoryById(serviceCreate.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("CategoryNotFound")));
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



}
