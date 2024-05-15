package com.adagency.dbwork.service;

import com.adagency.dbwork.jparepo.RoleRepository;
import com.adagency.model.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class RoleService {
    private final RoleRepository roleRepository;
    @Autowired
    public RoleService(RoleRepository roleRepository){
        this.roleRepository = roleRepository;
    }

    public Optional<Role> findById(long id) {
        return roleRepository.findById(id);
    }

    public void save(Role role){
        roleRepository.save(role);
    }

    public Optional<Role> findByName(String name){
        return roleRepository.findByName(name);
    }

    public List<Role> getAll(){
        return roleRepository.findAll();
    }
    
}
