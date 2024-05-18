package com.adagency.dbwork.service;

import com.adagency.config.MvcConfig;
import com.adagency.dbwork.jparepo.ServiceRepository;
import com.adagency.model.dto.mediafile.MediaFileCreate;
import com.adagency.model.dto.service.ServiceCreate;
import com.adagency.model.entity.MediaFile;
import com.adagency.model.mapper.service.ServiceMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import com.adagency.model.entity.Service ;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final MediaFileService mediaFileService;
    private final ServiceMapper serviceMapper;

    @Autowired
    public ServiceService(ServiceRepository serviceRepository, MediaFileService mediaFileService,
                          ServiceMapper serviceMapper){
        this.serviceRepository = serviceRepository;
        this.mediaFileService = mediaFileService;
        this.serviceMapper = serviceMapper;
    }

    @Transactional
    @SneakyThrows
    public void CreateService(ServiceCreate serviceCreate) throws IOException, Exception {
        Service service = serviceMapper.fromServiceCreateToService(serviceCreate);
        serviceRepository.save(service);
        List<MediaFile> createdFiles = serviceCreate.getFiles().stream()
                .map(mediaFileCreate -> {
                    try {
                        return mediaFileService.testCreateWithTransferFileToPathServer(
                                mediaFileCreate,
                                "ServiceMedia",
                                mediaFileCreate.getDescription(),
                                MvcConfig.RESOURCE_PATH + "images/Category/" + service.getId()
                                        + "/" + mediaFileCreate.getFile().getOriginalFilename(),
                                mediaFileCreate.getAlt()
                        );
                })//fixme пееределать в foreach
                .collect(Collectors.toList());
    }



}
