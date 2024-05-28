package com.adagency.Controllers;

import com.adagency.dbwork.service.BaseModelPersonService;
import com.adagency.dbwork.service.CategoryService;
import com.adagency.dbwork.service.ClientService;
import com.adagency.dbwork.service.ServiceService;
import com.adagency.model.dto.client.ClientRegistrationDTO;
import com.adagency.model.dto.person.LoginModel;
import com.adagency.model.entity.BaseModelPerson;
import com.adagency.model.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;


@Controller
public class HomeController {

	private final AuthenticationManager authenticationManager;
	private final ClientService clientService;
	private final BaseModelPersonService baseModelPersonService;
	private final CategoryService categoryService;
	private final ServiceService serviceService;

	@Autowired
	public HomeController(AuthenticationManager authenticationManager,ClientService clientService,
	                      BaseModelPersonService baseModelPersonService, CategoryService categoryService,
	                      ServiceService serviceService) {
		this.authenticationManager = authenticationManager;
		this.clientService = clientService;
		this.baseModelPersonService =  baseModelPersonService;
		this.categoryService = categoryService;
		this.serviceService = serviceService;
	}

	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("categories",categoryService.getCategoryViewListForClient());
		return "Home/index";
	}

	@GetMapping("/register")
	public String register(Model model, Authentication authentication) {
		model.addAttribute("clientRegistrationDTO", new ClientRegistrationDTO());
		return "Home/register";
	}

	@PostMapping("/register")
	public String register(@ModelAttribute("clientRegistrationDTO") @Valid ClientRegistrationDTO clientRegistrationDTO,
	                       BindingResult result, Model model, Authentication authentication) {
		if (result.hasErrors()) {
			return "Home/register";
		}
		
		String role = authentication == null ? "ROLE_ANONYMOUS"
				: ((CustomUserDetails) authentication.getPrincipal()).getAuthorities().iterator().next().getAuthority();

		try {
			clientService.save(clientRegistrationDTO);
		}catch (Exception e) {
			model.addAttribute("emailError", e.getMessage());
			return "Home/register";
		}
		
		if(role.equals("ROLE_ADMIN") || role.equals("ROLE_AGENT"))
		{
			model.addAttribute("success", "Клиент создан");
			model.addAttribute("clientRegistrationDTO", new ClientRegistrationDTO());
			return "Home/register";
		}else{
			try {
				SecurityContextHolder.getContext().setAuthentication(
						authenticationManager.authenticate(
								new UsernamePasswordAuthenticationToken(clientRegistrationDTO.getEmail(), clientRegistrationDTO.getPassword())
						)
				);
				return "redirect:/";
			}catch (Exception e){
				model.addAttribute("emailError", e.getMessage());
				return "Home/register";
			}
		}
	}
	
	@GetMapping("/login")
	public String login(Model model) {
		model.addAttribute("loginModel", new LoginModel());
		return "Home/login";
	}
	
	@PostMapping("/login")
	public String login(@ModelAttribute("loginModel") LoginModel loginModel,
	                    BindingResult result, Model model) {
		if (result.hasErrors()) {
			return "Home/login";
		}
		try {
			Optional<BaseModelPerson> baseModelPerson = baseModelPersonService.findByEmail(loginModel.getEmail());
			if (baseModelPerson.isPresent() && !baseModelPerson.get().isDeleteFlag()) {
				SecurityContextHolder.getContext().setAuthentication(
						authenticationManager.authenticate(
								new UsernamePasswordAuthenticationToken(
										loginModel.getEmail(),
										loginModel.getPassword()
								)
						)
				);
				return "redirect:/";
			} else {
				model.addAttribute("error", baseModelPerson.isPresent() ? "Пользователь удален" : "Пользователь с такой почтой не найден");
				return "Home/login";
			}
		} catch (Exception e) {
			model.addAttribute("error", e.getMessage());
			return "Home/login";
		}
	}


	@GetMapping("/logout")
	public String logout() {
		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			SecurityContextHolder.getContext().setAuthentication(null);
		}
		return "redirect:/";
	}

	@GetMapping("/category/{id}")
	public String viewCategory(@PathVariable Long id, Model model){
		model.addAttribute("category", categoryService.getCategoryViewWithServices(id));
		return "Home/category";
	}
	
	@GetMapping("/service/{id}")
	public String viewService(@PathVariable Long id, Model model){
		model.addAttribute("service",serviceService.getServiceView(id));
		return "Home/service";
	}

}
