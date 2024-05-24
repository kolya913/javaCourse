package com.adagency.Controllers;

import com.adagency.dbwork.service.CategoryService;
import com.adagency.dbwork.service.ServicePricingService;
import com.adagency.dbwork.service.ServiceService;
import com.adagency.dbwork.service.StatusService;
import com.adagency.model.dto.category.CategoryCreateDTO;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.adagency.model.dto.category.CategoryView;
import com.adagency.model.dto.mediafile.MediaFileCreate;
import com.adagency.model.dto.service.ServiceCreate;
import com.adagency.model.dto.service.ServiceEdit;
import com.adagency.model.dto.servicepricing.ServicePricingCreateEditList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.stream.Stream;


@Controller
public class ServicesController {

	private final CategoryService categoryService;
	private final StatusService statusService;
	private final ServiceService serviceService;
	private final ServicePricingService servicePricingService;
	
	@Autowired
	public ServicesController(CategoryService categoryService, StatusService statusService,
	                          ServiceService serviceService, ServicePricingService servicePricingService){
		this.categoryService = categoryService;
		this.statusService = statusService;
		this.serviceService = serviceService;
		this.servicePricingService = servicePricingService;
	}
	
	@GetMapping("/managecategories")
	public String categories(Model model){
		model.addAttribute("categories", categoryService.getListCategoryView()); //todo в представление добавить отображение услуг и поиск по ним
		return "Services/categories";
	}
	
	@GetMapping("/managecategories/createcategory")
	public String createCategory(Model model){
		model.addAttribute("categoryCreateDTO", new CategoryCreateDTO());
		return "Services/createCategory";
	}

	@PostMapping("/managecategories/createcategory")
	public String createCategory(@ModelAttribute("categoryCreateDTO") @Valid CategoryCreateDTO categoryCreateDTO, BindingResult result, 
		Model model){

		model.addAttribute("categoryCreateDTO", categoryCreateDTO);

		if(result.hasErrors()){
			model.addAttribute("error", result.getAllErrors());
			return "Services/createCategory";
		}
		try{
		categoryService.create(categoryCreateDTO);
		} catch(Exception e){
			model.addAttribute("error", e.getMessage()); //todo сделать отображение результата
		}
		return "Services/createCategory";
	}


	@GetMapping("/managecategories/categoryInfo/{id}")
	public String categoryInfo(@PathVariable Long id, Model model, HttpSession session) {
		try{
		model.addAttribute("category", categoryService.getCategoryViewWithServices(id));
		}catch (EntityNotFoundException e){
			throw  new ResponseStatusException(HttpStatus.BAD_REQUEST, "NotFound");
			//model.addAttribute("error", e.getMessage());
		}
		return "Services/infoCategory";
	}


	@GetMapping("/managecategories/categoryEdit/{id}")
	public String categoryEdit(@PathVariable Long id, Model model) {
		try{
		model.addAttribute("category", categoryService.getCategoryView(id));
		model.addAttribute("status", statusService.getAll());
		}catch (EntityNotFoundException e){
			model.addAttribute("error", e.getMessage());
		}
		return "Services/editCategory";
	}
	
	@PostMapping("/managecategories/categoryEdit")
	public String categoryEdit(@ModelAttribute("category") CategoryView categoryView, Model model) {
		try{
			model.addAttribute("category", categoryService.updateCategory(categoryView)); //todo сделать сообщение об ошибке и успехе
		}catch (EntityNotFoundException | IOException e){
			model.addAttribute("category",categoryView);
			model.addAttribute("status", statusService.getAll());
			model.addAttribute("error", e.getMessage());
		}
		return "Services/editCategory";
	}
	
	@GetMapping("/managecategories/createservice/{id}")
	public String createService(@PathVariable Long id, Model model) {
		ServiceCreate serviceCreate = new ServiceCreate();
		serviceCreate.setCategoryId(id);
		model.addAttribute("service",serviceCreate);
		return "Services/createservice";
	}


	@PostMapping("/managecategories/createservice")
	public String createService(@ModelAttribute("service") @Valid ServiceCreate serviceCreate, BindingResult result, Model model) {
		if(result.hasErrors()){
			model.addAttribute("error","ошибка result");
			model.addAttribute("service",serviceCreate);
			return "Services/createservice";
		}

		long mainFilesCount = serviceCreate.getFiles().stream()
				.filter(MediaFileCreate::isMain)
				.count();

		if(mainFilesCount > 1){
			model.addAttribute("error", "Основных файлов больше 1");
			model.addAttribute("service",serviceCreate);
			return "Services/createservice";
		} else if (mainFilesCount == 0) {
			model.addAttribute("error", "Должен быть выбран хотябы один файл");
			model.addAttribute("service",serviceCreate);
			return "Services/createservice";
		}
		
		try {
			serviceService.CreateService(serviceCreate);
			model.addAttribute("service",new ServiceCreate(serviceCreate.getCategoryId()));
			model.addAttribute("success","Запись создана");
		} catch (IOException e) {
			model.addAttribute("error",e.getMessage());
		} catch (Exception e) {
			model.addAttribute("error",e.getMessage());
		}
		return "Services/createservice";
	}

	@GetMapping("/managecategories/infoservice/{id}")
	public String infoService(@PathVariable Long id, Model model){
		model.addAttribute("service",serviceService.getServiceView(id));
		return "Services/infoservice";
	}
	
	@GetMapping("/managecategories/editservice/{id}")
	public String editService(@PathVariable Long id, Model model){
		model.addAttribute("service", serviceService.getServiceEdit(id));
		model.addAttribute("statusList", statusService.getAll());
		return "Services/editServices";
	}

	@PostMapping("/managecategories/editservice")
	public String editService(@ModelAttribute("service") @Valid  ServiceEdit serviceEdit, BindingResult result,
							  Model model){
		if(result.hasErrors()){
			model.addAttribute("error","result Error");
			model.addAttribute("service", serviceEdit);
			return "Services/editServices";
		}
		
/*		Stream<MediaFileCreate> mediaFileCreatesStream = (serviceEdit.getMediaFileCreates() != null) ?
				serviceEdit.getMediaFileCreates().stream().filter(MediaFileCreate::isMain) :
				Stream.empty();*/
		
		if(Stream.concat(
				serviceEdit.getMediaFiles().stream().parallel().filter(mediaFile -> mediaFile.isMain() && !mediaFile.isDeleteFlag()),
				(serviceEdit.getMediaFileCreates() != null) ?
						serviceEdit.getMediaFileCreates().stream().filter(MediaFileCreate::isMain) : Stream.empty())
				.count() != 1){
			model.addAttribute("error","должен быть хотя бы один основной элемент");
			model.addAttribute("service", serviceEdit);
			return "Services/editServices";
		}

		try {
			serviceService.updateService(serviceEdit);
			model.addAttribute("error","УСПЕЗХЗ!1!!!!!!1?         Лист создания " + serviceEdit.getMediaFileCreates().toArray().length);
		}catch (Exception e){
			model.addAttribute("error", e.getMessage());
		}
		
		model.addAttribute("service",serviceService.getServiceEdit(serviceEdit.getId()));
		model.addAttribute("statusList", statusService.getAll());
		return "Services/editServices";
	}


	@GetMapping("/managecategories/infoservice/{id}/editpricing")
	public String createPricing(@PathVariable Long id, Model model){
		if(serviceService.checkExsist(id)){
			ServicePricingCreateEditList servicePricingCreateEditList = new ServicePricingCreateEditList();
			servicePricingCreateEditList.setServiceId(id);
			servicePricingCreateEditList.setServicePricingEditList(serviceService.getServicesPricingsToEdit(id));
			model.addAttribute("servicePricing", servicePricingCreateEditList);
			model.addAttribute("statusList",statusService.getAll());
		}else{
			model.addAttribute("error","Услугу не найдена с id=" + id);
		}
		return "Services/createPricing";
	}
	
	@PostMapping("/managecategories/editpricing")
	public String createPricing(@ModelAttribute("servicePricing") ServicePricingCreateEditList servicePricingCreateEditList, Model model){
		model.addAttribute("statusList",statusService.getAll());
		model.addAttribute("testMessage","ok");
		servicePricingService.createUpdate(servicePricingCreateEditList);
		return "Services/createPricing";
	}


}
