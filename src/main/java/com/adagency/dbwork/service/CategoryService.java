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
    public void create(CategoryCreateDTO categoryCreateDTO) throws Exception { //todo Status
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
            categoryCreateDTO.getFile().getAlt()));
    }
    
    @Transactional
    public CategoryView getCategoryView(Long id){
        Optional<Category> category = categoryRepository.findById(id);
        if(!category.isPresent()){
            throw new EntityNotFoundException("CategoryWithId=" + id + "NotFound");
        }else {
            CategoryView categoryView = categoryMapper.fromCategoryToCategoryView(category.get());
            categoryView.setFile(mediaFileService.getMediaFileView(category.get().getPicture()));
            return categoryView;
        }
    }

    @Transactional
    public CategoryView getCategoryViewWithServices(Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (!category.isPresent()) {
            throw new EntityNotFoundException("CategoryNotFound");
        } else {
            CategoryView categoryView = categoryMapper.fromCategoryToCategoryView(category.get());
            categoryView.setFile(mediaFileService.getMediaFileView(category.get().getPicture()));

            if (category.get().getServices() != null && !category.get().getServices().isEmpty()) {
                categoryView.setServices(category.get().getServices().stream().parallel()
                        .map(service -> {
                            ServiceView serviceView = serviceMapper.fromServiceToServiceView(service);
                            if (service.getMediaFiles() != null && !service.getMediaFiles().isEmpty()) {
                                List<MediaFileView> mediaViews = service.getMediaFiles().stream().parallel()
                                        .filter(mediaFile -> mediaFile.isMain())
                                        .map(mediaFileService::getMediaFileView)
                                        .collect(Collectors.toList());
                                serviceView.setMedia(mediaViews);
                            }
                            return serviceView;
                        }).collect(Collectors.toList()));
            }
            return categoryView;
        }
    }

    
    
    
    @Transactional
    public List<CategoryView> getListCategoryView(){
        return categoryRepository.findAll().stream().parallel()
                .map(category -> {
                    CategoryView categoryView = categoryMapper.fromCategoryToCategoryView(category);
                    if(category.getPicture() != null)
                        categoryView.setFile(mediaFileService.getMediaFileView(category.getPicture()));
                    return categoryView;
                })
                .collect(Collectors.toList());
    }
    
    
    
    @Transactional
    public void deleteCategoryById(Long id){
        Optional<Category> category = categoryRepository.findById(id);
        if(!category.isPresent()){
            throw new EntityNotFoundException("CategoryWithId=" + id + "NotFound");
        }else{
            category.get().setDeleteFlag(true);
            categoryRepository.save(category.get());
        }
    }
    
    
    @Transactional
    public CategoryView updateCategory(CategoryView categoryView) throws IOException {
        Optional<Category> category = categoryRepository.findById(categoryView.getId());
        if(!category.isPresent()){
            throw new EntityNotFoundException("CategoryWithId=" + category.get().getId() + "NotFound");
        }else{
            categoryMapper.updateCategory(category.get(), categoryView);
            category.get().setPicture(mediaFileService.update(categoryView.getFile().getFileId(),
                    categoryView.getFile().getFileDescription(),categoryView.getFile().getFileAlt(),
                    categoryView.getFile().getFile()));
            category.get().setDateLastUpdate(new Date());
            if (categoryView.getStatusId() != null){
                Optional<Status> status =statusService.findById(categoryView.getStatusId());
                if(!status.isPresent()){
                    throw new EntityNotFoundException("StatusNotFound");
                }else{
                    category.get().setStatus(status.get());
                }
            }
            categoryRepository.save(category.get());
            categoryView.setFile(mediaFileService.getMediaFileView(category.get().getPicture()));
            return categoryView;
        }
    }

    public Optional<Category> getCategoryById(long id){
        return categoryRepository.findById(id);
    }

    @Transactional
    public List<ClientCategoryView> getCategoryViewListForClient(){
        List<Category> categories = categoryRepository.findAllByStatus_Id(1L);
        return categories.stream().map(category -> {
            ClientCategoryView clientCategoryView = categoryMapper.fromCategoryToClientCategoryView(category);
            clientCategoryView.setFile(mediaFileService.getMediaFileView(category.getPicture()));
            clientCategoryView.setServices(category.getServices().stream().map(serviceMapper::fromServiceToServiceSimpleView).collect(Collectors.toList()));
            return clientCategoryView;}).collect(Collectors.toList());
        
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
                                                service.getServicePricings().stream().map(servicePricing -> {
                                                    return ServicePricingView.builder()
                                                            .id(servicePricing.getId())
                                                            .serviceName(servicePricing.getServiceName())
                                                            .price(servicePricing.getPrice())
                                                            .minPeriodInDays(servicePricing.getMinPeriodInDays())
                                                            .maxPeriodInDays(servicePricing.getMaxPeriodInDays())
                                                            .circulation(servicePricing.getCirculation())
                                                            .build();
                                                }).collect(Collectors.toList())
                                        ).build();
                                return serviceView;
                            }).collect(Collectors.toList())
                    ).build();
            return clientCategory;
        }).collect(Collectors.toList());
    }
    
    
}
