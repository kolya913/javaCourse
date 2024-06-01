package com.adagency.dbwork.service;

import com.adagency.config.MvcConfig;
import com.adagency.dbwork.jparepo.CategoryRepository;
import com.adagency.model.dto.category.CategoryCreateDTO;
import com.adagency.model.dto.category.CategoryView;
import com.adagency.model.dto.category.ClientCategoryView;
import com.adagency.model.dto.category.ClientSimpleCategory;
import com.adagency.model.dto.mediafile.MediaFileView;
import com.adagency.model.dto.service.ServiceSimpleView;
import com.adagency.model.dto.service.ServiceView;
import com.adagency.model.dto.servicepricing.ServicePricingView;
import com.adagency.model.entity.MediaFile;
import com.adagency.model.entity.Status;
import com.adagency.model.mapper.category.CategoryMapper;
import com.adagency.model.mapper.service.ServiceMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import com.adagency.model.entity.Category;
import org.springframework.stereotype.Service;


@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final MediaFileService mediaFileService;
    private final StatusService statusService;
    private final ServiceMapper serviceMapper;
    
    @Autowired
    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper,
                           MediaFileService mediaFileService, StatusService statusService,
                           ServiceMapper serviceMapper){
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.mediaFileService = mediaFileService;
        this.statusService = statusService;
        this.serviceMapper = serviceMapper;
    }
    
    @Transactional
    public void create(CategoryCreateDTO categoryCreateDTO) throws Exception {
        Category category = categoryMapper.createCategoryDTOTOCategory(categoryCreateDTO);
        category.setDeleteFlag(false);
        category.setDateCreateAt(new Date());
        category.setDateLastUpdate(new Date());
        category.setStatus(statusService.findByName("Awaiting approval").get());

        categoryRepository.save(category);

        category.setPicture(mediaFileService.testCreateWithTransferFileToPathServer(
            categoryCreateDTO.getFile(),
            "CategoryPicture", 
            categoryCreateDTO.getFile().getDescription(), 
            MvcConfig.RESOURCE_PATH + "images/Category/" + category.getId()
                    + "/" + categoryCreateDTO.getFile().getFile().getOriginalFilename(),
            categoryCreateDTO.getFile().getAlt()
                )
        );
    }
    
    @Transactional
    public CategoryView getCategoryView(Long id){
        return categoryRepository.findById(id)
                .map(category -> {
                    CategoryView categoryView = categoryMapper.fromCategoryToCategoryView(category);
                    categoryView.setFile(mediaFileService.getMediaFileView(category.getPicture()));
                    return categoryView;
                }).orElseThrow(() -> new EntityNotFoundException("CategoryNotFound"));
    }
    
    
    @Transactional
    public CategoryView getCategoryViewWithServices(Long id) {
        return categoryRepository.findById(id)
                .map(category -> {
                    CategoryView categoryView = categoryMapper.fromCategoryToCategoryView(category);
                    categoryView.setFile(mediaFileService.getMediaFileView(category.getPicture()));
                    
                    List<ServiceView> serviceViews = category.getServices().stream()
                            .map(service -> {
                                ServiceView serviceView = serviceMapper.fromServiceToServiceView(service);
                                List<MediaFileView> mediaViews = service.getMediaFiles().stream()
                                        .filter(MediaFile::isMain)
                                        .map(mediaFileService::getMediaFileView)
                                        .collect(Collectors.toList());
                                serviceView.setMedia(mediaViews);
                                return serviceView;
                            }).collect(Collectors.toList());
                    
                    categoryView.setServices(serviceViews);
                    return categoryView;
                }).orElseThrow(() -> new EntityNotFoundException("CategoryNotFound"));
    }
    

    @Transactional
    public List<CategoryView> getListCategoryView(){
        return categoryRepository.findAll().stream()
                .map(category -> {
                    CategoryView categoryView = categoryMapper.fromCategoryToCategoryView(category);
                    if(category.getPicture() != null)
                        categoryView.setFile(mediaFileService.getMediaFileView(category.getPicture()));
                    return categoryView;
                })
                .collect(Collectors.toList());
    }
    
    
    @Transactional
    public CategoryView updateCategory(CategoryView categoryView) throws IOException {
        Category category = categoryRepository.findById(categoryView.getId())
                .orElseThrow(() -> new EntityNotFoundException("CategoryNotFound"));
        categoryMapper.updateCategory(category, categoryView);
        if (categoryView.getFile() != null) {
            category.setPicture(mediaFileService.update(
                    categoryView.getFile().getFileId(),
                    categoryView.getFile().getFileDescription(),
                    categoryView.getFile().getFileAlt(),
                    categoryView.getFile().getFile()));
        }
        category.setDateLastUpdate(new Date());
        if (categoryView.getStatusId() != null) {
            Status status = statusService.findById(categoryView.getStatusId())
                    .orElseThrow(() -> new EntityNotFoundException("StatusNotFound"));
            category.setStatus(status);
        }
        categoryRepository.save(category);
        categoryView.setFile(mediaFileService.getMediaFileView(category.getPicture()));
        return categoryView;
    }


    public Optional<Category> getCategoryById(long id){
        return categoryRepository.findById(id);
    }
    
    @Transactional
    public List<ClientCategoryView> getCategoryViewListForClient() {
        List<Category> categories = categoryRepository.findAllByStatus_Id(1L);
        return categories.stream()
                .filter(category -> category.getServices().stream()
                        .anyMatch(service -> service.getStatus().getId() == 1L &&
                                service.getServicePricings().stream()
                                        .anyMatch(servicePricing -> servicePricing.getStatus().getId() == 1L)))
                .map(category -> {
                    ClientCategoryView clientCategoryView = categoryMapper.fromCategoryToClientCategoryView(category);
                    clientCategoryView.setFile(mediaFileService.getMediaFileView(category.getPicture()));
                    clientCategoryView.setServices(category.getServices().stream()
                            .filter(service -> service.getStatus().getId() == 1L)
                            .map(serviceMapper::fromServiceToServiceSimpleView)
                            .collect(Collectors.toList()));
                    return clientCategoryView;
                }).collect(Collectors.toList());
    }

    
    @Transactional
    public List<ClientSimpleCategory> getCategoryToPreCreateOrder() {
        List<Category> categories = categoryRepository.findAllByStatus_Id(1L);
        
        return categories.stream().map(category -> {
            ClientSimpleCategory clientCategory = ClientSimpleCategory.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .clientSimpleCategoryList(
                            category.getServices().stream().map(service -> {
                                ServiceSimpleView serviceView = ServiceSimpleView.builder()
                                        .id(service.getId())
                                        .name(service.getName())
                                        .servicePricingViewList(
                                                service.getServicePricings().stream().map(servicePricing ->
                                                        ServicePricingView.builder()
                                                        .id(servicePricing.getId())
                                                        .serviceName(servicePricing.getServiceName())
                                                        .price(servicePricing.getPrice())
                                                        .minPeriodInDays(servicePricing.getMinPeriodInDays())
                                                        .maxPeriodInDays(servicePricing.getMaxPeriodInDays())
                                                        .circulation(servicePricing.getCirculation())
                                                        .build()).collect(Collectors.toList())
                                        ).build();
                                return serviceView;
                            }).collect(Collectors.toList())
                    ).build();
            return clientCategory;
        }).collect(Collectors.toList());
    }
    
    
}
