package com.adagency.dbwork.service;

import com.adagency.dbwork.jparepo.WorkerRepository;
import com.adagency.model.dto.person.UserProfileForm;
import com.adagency.model.dto.worker.WorkerCreateDTO;
import com.adagency.model.entity.BaseModelPerson;
import com.adagency.model.entity.Worker;
import com.adagency.model.mapper.worker.WorkerMapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
public class WorkerService {

    private final WorkerRepository workerRepository;
    private final WorkerMapperImpl workerMapper;
    private final PositionService positionService;
    private final RoleService roleService;

    @Lazy
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;


    @Autowired
    public WorkerService(WorkerRepository workerRepository,  WorkerMapperImpl workerMapper,
                         RoleService roleService, PositionService positionService){
        this.workerRepository = workerRepository;
        this.workerMapper = workerMapper;
        this.roleService = roleService;
        this.positionService = positionService;
    }

    public Optional<Worker> findById(Long id) {
        return workerRepository.findById(id);
    }

    public Optional<Worker> findByEmail(String email){
        return workerRepository.findByEmail(email);
    }

    public void save(WorkerCreateDTO workerCreateDTO){

        Worker worker = workerMapper.fromWorkerCreateDTOTOWorker(workerCreateDTO);
        worker.setPassword(bCryptPasswordEncoder.encode(workerCreateDTO.getPassword()));
        worker.setDateCreate(new Date());
        worker.setDeleteFlag(false);
        workerRepository.save(worker);
    }
    
    @Transactional
    public void update(UserProfileForm userProfileForm){
        workerRepository.findById(userProfileForm.getId()).ifPresentOrElse(person -> {
            workerMapper.fromUserProfileFormToWorker(userProfileForm, person);
            if(userProfileForm.getPositionS() != null & person.getPosition().getId() != userProfileForm.getPositionS()){
                person.setPosition((positionService.findById(userProfileForm.getPositionS())).get());
            }
            if(userProfileForm.getRoleS() != null & person.getRole().getId() != userProfileForm.getRoleS() ){
                person.setRole((roleService.findById(userProfileForm.getRoleS())).get());
            }
            if(userProfileForm.getPassword()!=null & !userProfileForm.getPassword().isEmpty()){
                person.setPassword(bCryptPasswordEncoder.encode(userProfileForm.getPassword()));
            }
            workerRepository.save(person);
        }, () -> {
            throw new EntityNotFoundException("Worker with ID " + userProfileForm.getId() + " not found");
        });
    }
    
    
    public void create(WorkerCreateDTO workerCreateDTO){
        Worker worker = workerMapper.fromWorkerCreateDTOTOWorker(workerCreateDTO);
        worker.setPassword(bCryptPasswordEncoder.encode(workerCreateDTO.getPassword()));
        worker.setDateCreate(new Date());
        worker.setDeleteFlag(false);
        worker.setPosition(positionService.findById(workerCreateDTO.getPositionS()).get());
        worker.setRole(roleService.findById(workerCreateDTO.getPositionS()).get());
        workerRepository.save(worker);
    }


    @Transactional
    public UserProfileForm getProfileForm(Long id){
        return workerMapper.fromWorkerToUserProfileForm(workerRepository.findById(id).get());
    }
    
}