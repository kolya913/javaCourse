package com.adagency.dbwork.service;

import com.adagency.model.dto.client.ClientRegistrationDTO;
import com.adagency.model.dto.person.UserProfileForm;
import com.adagency.model.entity.BaseModelPerson;
import com.adagency.model.entity.Client;
import com.adagency.dbwork.jparepo.ClientRepository;
import com.adagency.model.mapper.client.ClientMapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class ClientService {

    @Lazy
    @Autowired
    private RoleService roleService;

    @Lazy
    @Autowired
    private BaseModelPersonService baseModelPersonService;

    private final ClientRepository clientRepository;

    private final ClientMapperImpl clientMapperImpl;

    @Lazy
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    @Autowired
    public ClientService(ClientRepository clientRepository,RoleService roleService, ClientMapperImpl clientMapperImpl) {
        this.clientRepository = clientRepository;
        this.clientMapperImpl = clientMapperImpl;
    }
    
    public Optional<Client> findById(Long id) {
        return clientRepository.findById(id);
    }

    @Transactional
    public UserProfileForm getProfileForm(Long id){
        return clientMapperImpl.fromClientToUserProfileForm(clientRepository.findById(id).get());
    }
    
    @Transactional
    public void update(UserProfileForm userProfileForm){
        if(!clientRepository.findById(userProfileForm.getId()).isPresent()){
            throw new EntityNotFoundException("ClientNotFound");
        }
        clientRepository.findById(userProfileForm.getId()).ifPresent(person ->{
            clientMapperImpl.fromUserProfileFormToClient(userProfileForm, person);
            if(userProfileForm.getPassword() != null & !userProfileForm.getPassword().isEmpty()){
                person.setPassword(bCryptPasswordEncoder.encode(userProfileForm.getPassword()));
            }
            if(userProfileForm.getRoleS() != null & !Objects.equals(userProfileForm.getRoleS(), person.getRole().getId())){
                person.setRole((roleService.findById(userProfileForm.getId())).get());
            }
        });
    }
    
    @Transactional
    public void save(ClientRegistrationDTO clientRegister) throws Exception {
        List<BaseModelPerson> baseModelPersonList = baseModelPersonService.findByEmailOrPhoneNumber(clientRegister.getEmail(), clientRegister.getPhoneNumber());
        if(baseModelPersonList.isEmpty()){
            Client client = clientMapperImpl.fromRegistrationDtoToCleint(clientRegister);
            client.setDateCreate(new Date());
            client.setDeleteFlag(false);
            client.setPassword(bCryptPasswordEncoder.encode(client.getPassword()));
            client.setRole(roleService.findByName("CLIENT").get());
            clientRepository.save(client);
        }
        else{
            boolean[] checks = {false, false};

            baseModelPersonList.forEach(person -> {
                if (person.getEmail().equals(clientRegister.getEmail())) {
                    checks[0] = true;
                }
                if (person.getPhoneNumber().equals(clientRegister.getPhoneNumber())) {
                    checks[1] = true;
                }
            });
            if (checks[0] && checks[1]) {
                throw new Exception("EmailAndPhoneNumberAlreadyExist");
            } else if (checks[0]) {
                throw new Exception("EmailAlreadyExists");
            } else if (checks[1]) {
                throw new Exception("PhoneNumberAlreadyExists");
            }
        }
    }

//
//    public void save(Client client, boolean nopassword) {
//        clientRepository.save(client);
//    }
//
//    public List<Client> getAll(){
//        return clientRepository.findAll();
//    }

}
