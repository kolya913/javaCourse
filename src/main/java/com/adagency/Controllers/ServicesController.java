package com.adagency.Controllers;

import com.adagency.dbwork.service.CategoryService;
import com.adagency.dbwork.service.ServiceService;
import com.adagency.dbwork.service.StatusService;
import com.adagency.model.dto.category.CategoryCreateDTO;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.adagency.model.dto.category.CategoryView;
import com.adagency.model.dto.service.ServiceCreate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;


@Controller
public class ServicesController {

	private final CategoryService categoryService;
	private final StatusService statusService;
	private final ServiceService serviceService;
	
	@Autowired
	public ServicesController(CategoryService categoryService, StatusService statusService,
	                          ServiceService serviceService){
		this.categoryService = categoryService;
		this.statusService = statusService;
		this.serviceService = serviceService;
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
		model.addAttribute("category", categoryService.getCategoryView(id));
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
	public String createService(@ModelAttribute("service") ServiceCreate serviceCreate, BindingResult result, Model model) {
		if(result.hasErrors()){
			model.addAttribute("error","ошибкаresult");
			model.addAttribute("service",serviceCreate);
			return "Services/createservice";
		}
		
		//model.addAttribute("error","нетуошибки, колво файлов: " + serviceCreate.getFiles().toArray().length);
		try {
			serviceService.CreateService(serviceCreate);
		} catch (IOException e) {
			model.addAttribute("error",e.getMessage());
		} catch (Exception e) {
			model.addAttribute("error",e.getMessage()); //todo view success or error и переход на страницу с темже categoryid
		}
		return "Services/createservice";
	}
	
	
	
}
