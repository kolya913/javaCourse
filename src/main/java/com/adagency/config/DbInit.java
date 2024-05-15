package com.adagency.config;

import com.adagency.dbwork.service.PositionService;
import com.adagency.dbwork.service.RoleService;
import com.adagency.dbwork.service.StatusService;
import com.adagency.dbwork.service.WorkerService;
import com.adagency.model.dto.worker.WorkerCreateDTO;
import com.adagency.model.entity.Position;
import com.adagency.model.entity.Role;
import com.adagency.model.entity.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;


@Component
public class DbInit implements ApplicationListener<ContextRefreshedEvent> {
    
    
    private final   RoleService roleService;
    
    private final WorkerService workerService;
    
    private final  PositionService positionService;
    
    private final StatusService statusService;


    @Autowired
    public DbInit(RoleService roleService,WorkerService workerService,PositionService positionService,
                  StatusService statusService) {
        this.roleService = roleService;
        this.workerService = workerService;
        this.positionService = positionService;
        this.statusService = statusService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if(!roleService.findByName("CLIENT").isPresent()){
            Role clientRole = new Role();
            clientRole.setName("CLIENT");
            roleService.save(clientRole);
        }

        

        if(!roleService.findByName("AGENT").isPresent()){
            Role agentRole = new Role();
            agentRole.setName("AGENT");
            roleService.save(agentRole);
        }


        Role adminRole = new Role();
        adminRole.setName("ADMIN");

        if(!roleService.findByName("ADMIN").isPresent()){
            roleService.save(adminRole);
        }

        Position position = new Position();
        
        Position position2 = new Position();
        
        if(!positionService.findByName("Администратор").isPresent()){
            position.setName("Администратор");  //TODO когда будет positionRegisterDTO надо передалать создание должности
            positionService.save(position);
        }
        
        if(!positionService.findByName("Агент").isPresent()){
            position2.setName("Агент");
            positionService.save(position2);
        }


         if(!workerService.findByEmail("a@a.a").isPresent()){
             WorkerCreateDTO createDTO = new WorkerCreateDTO();
             createDTO.setName("admin");
             createDTO.setMiddleName("admin");
             createDTO.setLastName("admin");
             createDTO.setEmail("a@a.a");
             createDTO.setPassword("admin");
             createDTO.setRole(adminRole);
             createDTO.setPhoneNumber("1234567890");
             createDTO.setSalary(5000);
             createDTO.setPosition(position);
             workerService.save(createDTO);

        }
         
         if(!statusService.findByName("Active").isPresent()){
             statusService.create(Status.builder().name("Active").build());
         }
        
        if(!statusService.findByName("Awaiting approval").isPresent()){
            statusService.create(Status.builder().name("Awaiting approval").build());
        }
        
        if(!statusService.findByName("Inactive").isPresent()){
            statusService.create(Status.builder().name("Inactive").build());
        }
        
        if(!statusService.findByName("Outdated").isPresent()){
            statusService.create(Status.builder().name("Outdated").build());
        }

    }
}
