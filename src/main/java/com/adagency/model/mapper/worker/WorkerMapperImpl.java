package com.adagency.model.mapper.worker;

import com.adagency.model.dto.person.UserProfileForm;
import com.adagency.model.dto.position.Position;
import com.adagency.model.dto.worker.WorkerCreateDTO;
import com.adagency.model.entity.Worker;
import org.springframework.stereotype.Component;

@Component
public class WorkerMapperImpl{
    public UserProfileForm fromWorkerToUserProfileForm(Worker worker){
        return UserProfileForm.builder()
                .id(worker.getId())
                .email(worker.getEmail())
                .password(null)
                .salary(worker.getSalary())
                .type(worker.getType())
                .phoneNumber(worker.getPhoneNumber())
                .name(worker.getName())
                .middleName(worker.getMiddleName())
                .lastName(worker.getLastName())
                .inn(worker.getInn())
                .position(Position.builder().id(worker.getPosition().getId()).name(worker.getPosition().getName()).build())
                .role(worker.getRole())
                .build();
    }
    
    public Worker fromWorkerCreateDTOTOWorker(WorkerCreateDTO workerCreateDTO){
        return Worker.builder()
                .name(workerCreateDTO.getName())
                .middleName(workerCreateDTO.getMiddleName())
                .lastName(workerCreateDTO.getLastName())
                .email(workerCreateDTO.getEmail())
                .phoneNumber(workerCreateDTO.getPhoneNumber())
                .inn(workerCreateDTO.getInn())
                .salary(workerCreateDTO.getSalary())
                .build();
    }
    
    public void fromUserProfileFormToWorker(UserProfileForm userProfileForm, Worker worker){
        worker.setName(userProfileForm.getName());
        worker.setMiddleName(userProfileForm.getMiddleName());
        worker.setLastName(userProfileForm.getLastName());
        worker.setEmail(userProfileForm.getEmail());
        worker.setPhoneNumber(userProfileForm.getPhoneNumber());
        if(userProfileForm.getSalary() != null){
            worker.setSalary(userProfileForm.getSalary());
        }
       }
    
    
    
}
