package com.adagency.Controllers;

import com.adagency.dbwork.service.*;
import com.adagency.model.dto.company.CompanyCreateDTO;
import com.adagency.model.dto.company.CompanyView;
import com.adagency.model.dto.person.SearchForm;
import com.adagency.model.dto.person.UserProfileForm;
import com.adagency.model.dto.worker.WorkerCreateDTO;
import com.adagency.model.entity.BaseModelPerson;
import com.adagency.model.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Objects;
import java.util.Optional;

//@SessionAttributes("searchForm")
@Controller
public class UserController {

    private final ClientService clientService;

    private final WorkerService workerService;

    private final BaseModelPersonService baseModelPersonService;
    
    private final PositionService positionService;
    
    private final RoleService roleService;

    private final CompanyService companyService;
    
    

    @Autowired
    public UserController(ClientService clientService,  WorkerService workerService,
                          BaseModelPersonService baseModelPersonService, PositionService positionService,
                          RoleService roleService, CompanyService companyService) {
        this.clientService = clientService;
        this.workerService = workerService;
        this.baseModelPersonService = baseModelPersonService;
        this.positionService = positionService;
        this.roleService = roleService;
        this.companyService = companyService;
    }

    @GetMapping("/mainmenu")
    public String mainMenu(Model model){
        return "User/mainmenu";
    }
    
    
    @GetMapping("/profile/{id}")
    public String profile(@PathVariable Long id, Model model, Authentication authentication) {
        CustomUserDetails currentUser = (CustomUserDetails) authentication.getPrincipal();
        Long currentUserId = currentUser.getUserId();
        String role = currentUser.getAuthorities().iterator().next().getAuthority();
        
        Optional<BaseModelPerson> baseModelPerson = baseModelPersonService.findById(id);
        if (!baseModelPerson.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        
        if (currentUserId.equals(id) || role.equals("ROLE_ADMIN")) {
            baseModelPerson.ifPresent(person -> {
                model.addAttribute("userProfileForm", person.getType().equals("Client") ?
                        clientService.getProfileForm(id) : workerService.getProfileForm(id));
                model.addAttribute("typeUser", person.getType());
            });
        } else if(role.equals("ROLE_AGENT")) {
            baseModelPerson.ifPresent(person -> {
                model.addAttribute("userProfileForm", person.getType().equals("Client") ?
                        clientService.getProfileForm(id) : workerService.getProfileForm(currentUserId));
                model.addAttribute("typeUser", person.getType().equals("Client") ?
                        "Client" : "Worker");
            });
        }
        return "User/profile";
    }
    
    @GetMapping("/profile/refactor/{id}")
    public String profileRefactor(@PathVariable Long id, Model model, Authentication authentication) {
        CustomUserDetails currentUser = (CustomUserDetails) authentication.getPrincipal();
        Long currentUserId = currentUser.getUserId();
        String role = currentUser.getAuthorities().iterator().next().getAuthority();
        Optional<BaseModelPerson> baseModelPerson = baseModelPersonService.findById(id);
        if(!baseModelPerson.isPresent()){
            return "User/profileRefactor";
        }
        baseModelPerson.ifPresent(person ->{
            if(role.equals("ROLE_ADMIN")){
                model.addAttribute("roles", roleService.getAll());
            }
            if(person.getType().equals("Worker")){
                model.addAttribute("positions", positionService.getAll());
            }
        });
        if (currentUserId.equals(id) || role.equals("ROLE_ADMIN")) {
            baseModelPerson.ifPresent(person -> {
                model.addAttribute("userProfileForm", person.getType().equals("Client") ?
                        clientService.getProfileForm(id) : workerService.getProfileForm(id));
                model.addAttribute("typeUser", person.getType());
            });

        } else if(role.equals("ROLE_AGENT")) {
            baseModelPerson.ifPresent(person -> {
                model.addAttribute("userProfileForm", person.getType().equals("Client") ?
                        clientService.getProfileForm(id) : workerService.getProfileForm(currentUserId));
                model.addAttribute("typeUser", person.getType().equals("Client") ?
                        "Client" : "Worker");
            });
        }
      
        return "User/profileRefactor";
    }

    @PostMapping("/profile/refactor/save")
    public String saveProfile(@ModelAttribute("userProfileForm") @Valid UserProfileForm userProfileForm,
                              BindingResult result, Model model, Authentication authentication ) {
        CustomUserDetails currentUser = (CustomUserDetails) authentication.getPrincipal();
        Long currentUserId = currentUser.getUserId();
        String role = currentUser.getAuthorities().iterator().next().getAuthority();
        
        if (result.hasErrors()) {
            model.addAttribute("validationErrors", result.getAllErrors());
            model.addAttribute("roles", roleService.getAll());
            model.addAttribute("positions", positionService.getAll());
            return "User/profileRefactor";
        }
        
        if(role.equals("ROLE_ADMIN")){
            workerService.findById(userProfileForm.getId()).ifPresent(person ->{
                workerService.update(userProfileForm);
            });
            clientService.findById(userProfileForm.getId()).ifPresent(person ->
                    clientService.update(userProfileForm));
        }
        
        if(role.equals("ROLE_CLIENT")){
            if(Objects.equals(currentUserId, userProfileForm.getId())){
                clientService.findById(userProfileForm.getId()).ifPresent(person ->
                        clientService.update(userProfileForm));
            }
        }
        
        if(role.equals("ROLE_AGENT")){
            workerService.findById(userProfileForm.getId()).ifPresent(person ->{
                workerService.update(userProfileForm);
            });
            clientService.findById(userProfileForm.getId()).ifPresent(person ->
                    clientService.update(userProfileForm));
        }

        return "redirect:/profile/"+userProfileForm.getId();
    }
    
    
    @GetMapping("/users")
    public String users(Model model,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "5") int size,
                        HttpSession session,
                        Authentication authentication) {
        SearchForm form = (SearchForm) session.getAttribute("searchForm");
        
        if (form == null) {
            form = new SearchForm();
            session.setAttribute("searchForm", form);
        }
        
        Pageable pageable = PageRequest.of(page, size);
        Page<BaseModelPerson> usersPage = baseModelPersonService.findUsersByCriteria(form, pageable);
        
        model.addAttribute("searchForm", form);
        model.addAttribute("usersPage", usersPage);
        model.addAttribute("size", size);
        return "User/users";
    }
    
    @PostMapping("/users")
    public String searchUsers(@ModelAttribute("searchForm") SearchForm searchForm,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "5") int size,
                              Model model,
                              HttpSession session,
                              Authentication authentication) {
        session.setAttribute("searchForm", searchForm);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<BaseModelPerson> usersPage = baseModelPersonService.findUsersByCriteria(searchForm, pageable);
        
        model.addAttribute("searchForm", searchForm);
        model.addAttribute("usersPage", usersPage);
        model.addAttribute("size", size);
        
        return "User/users"; //
    }
    
    @GetMapping("/deleteUser/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        try {
            baseModelPersonService.removeUser(id);
            return ResponseEntity.ok("Пользователь успешно удален");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body("Пользователь не найден");
        }
    }
    
    @GetMapping("/restoreUser/{id}")
    @ResponseBody
    public ResponseEntity<String> restoreUser(@PathVariable Long id) {
        try {
            baseModelPersonService.restoreUser(id);
            return ResponseEntity.ok("Пользователь успешно восстановлен");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body("Пользователь не найден");
        }
    }

    
    @GetMapping("/registerworker")
    public String registerWorker(Model model, Authentication authentication){
        model.addAttribute("positions", positionService.getAll());
        model.addAttribute("roles", roleService.getAll());
        model.addAttribute("workerCreateDTO", new WorkerCreateDTO());

        return "User/registerworker";
    }
    
    @PostMapping("/registerworker")
    public String registerWorker(@ModelAttribute @Valid WorkerCreateDTO  workerCreateDTO,BindingResult result, Model model){
        if(result.hasErrors()){
            model.addAttribute("positions", positionService.getAll());
            model.addAttribute("roles", roleService.getAll());
            model.addAttribute("registerWorker", workerCreateDTO);
        }
        model.addAttribute("positions", positionService.getAll());
        model.addAttribute("roles", roleService.getAll());
        model.addAttribute("registerWorker", new WorkerCreateDTO());
        workerService.create(workerCreateDTO);
        model.addAttribute("success", "Клиент создан");
        return "User/registerworker";
    }

    @GetMapping("/company")
    public String company(Model model){
        model.addAttribute("companies",companyService.getAll());
        return "User/company";
    }

    @GetMapping("/company/createcompany")
    public String createcompany( Model model) {
        model.addAttribute("createCompanyDTO", new CompanyCreateDTO());
        return "User/createcompany";
    }

    @PostMapping("/company/createcompany")
    public String createсompany(@ModelAttribute("createCompanyDTO") CompanyCreateDTO createCompanyDTO, Model model) {
        try {
            companyService.create(createCompanyDTO);
        }catch (Exception e){
            model.addAttribute("error", e.getMessage());
            return "User/createcompany";
        }
        return "redirect:/company";
    }
    
    @GetMapping("/company/info/{id}")
    public String infoCompany( @PathVariable Long id, Model model) {
        try {
            model.addAttribute("company", companyService.getCompanyView(id));
        }catch (EntityNotFoundException e){
            model.addAttribute("error", e.getMessage());
        }
        return "User/companyinfo";
    }
    
    @GetMapping("/company/delete/{id}")
    public String deleteCompany( @PathVariable Long id, Model model) {
        try {
            companyService.delete(id);
        }catch (Exception e){
            return "User/company";
        }
        return "redirect:/company";
    }
    
    @GetMapping("/company/edit/{id}")
    public String editCompany( @PathVariable Long id, Model model) {
        try {
            model.addAttribute("companyView",companyService.getCompanyView(id));
        }catch (EntityNotFoundException e){
            model.addAttribute("error","Компания с id = " + id + "не была найдена");
        }catch (Exception e){
            model.addAttribute("error", e.getMessage());
        }
        return "User/companyedit";
    }
    
    @PostMapping("/company/edit")
    public String editCompany(@ModelAttribute("companyView") CompanyView companyView, BindingResult result,
                              Model model) {
        try {
            companyService.update(companyView);
        }catch (Exception e){
            model.addAttribute("error", e.getMessage());
            return "User/companyedit";
        }
        return "redirect:/company";
    }

}
