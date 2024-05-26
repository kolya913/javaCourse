package com.adagency.dbwork.service;

import com.adagency.dbwork.jparepo.BaseModelPersonRepository;
import com.adagency.model.dto.person.SearchForm;
import com.adagency.model.dto.security.AuthorizePersonDTO;
import com.adagency.model.entity.BaseModelPerson;
import com.adagency.model.mapper.basemodelperson.BaseModelPersonMapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class BaseModelPersonService {
    private final BaseModelPersonRepository baseModelPersonRepository;
    private final BaseModelPersonMapperImpl baseModelPersonMapperImpl;

    @Autowired
    public BaseModelPersonService(BaseModelPersonRepository baseModelPersonRepository,
                                  BaseModelPersonMapperImpl baseModelPersonMapperImpl){
        this.baseModelPersonRepository = baseModelPersonRepository;
        this.baseModelPersonMapperImpl = baseModelPersonMapperImpl;
    }

    public AuthorizePersonDTO authorize(String email){
        return baseModelPersonMapperImpl.fromBMPtoAuthorize(baseModelPersonRepository.findByEmail(email).get());
    }

    public Optional<BaseModelPerson> findByEmail(String email) {
        return baseModelPersonRepository.findByEmail(email);
    }

    


    public List<BaseModelPerson> findByEmailOrPhoneNumber(String email, String phoneNumber){
        return baseModelPersonRepository.findByEmailOrPhoneNumber(email, phoneNumber);
    }

    @Transactional
    public Optional<BaseModelPerson> findById(Long id){
        return baseModelPersonRepository.findById(id);
    }


    public List<BaseModelPerson> getAll(){
        return baseModelPersonRepository.findAll();
    }
    
    

    
    public Page<BaseModelPerson> findUsersByCriteria(SearchForm searchForm, Pageable pageable) {
        return baseModelPersonRepository.findByCriteria(
                searchForm.getEmail(),
                searchForm.getName(),
                searchForm.getMiddleName(),
                searchForm.getLastName(),
                searchForm.getPhoneNumber(),
                searchForm.getInn(),
                searchForm.isClient(),
                searchForm.isWorker(),
                searchForm.isDeleteFlag(),
                pageable
        );
    }
    
    @Transactional
    public void removeUser(Long id){
        Optional<BaseModelPerson> baseModelPerson = baseModelPersonRepository.findById(id);
        if(baseModelPerson.isPresent()){
            baseModelPerson.get().setDeleteFlag(true);
            baseModelPersonRepository.save(baseModelPerson.get());
        }else {
            throw new EntityNotFoundException("PersonWithId=" + id + "NotFound");
        }
    }
    
    @Transactional
    public void restoreUser(Long id){
        Optional<BaseModelPerson> baseModelPerson = baseModelPersonRepository.findById(id);
        if(baseModelPerson.isPresent()){
            baseModelPerson.get().setDeleteFlag(false);
            baseModelPersonRepository.save(baseModelPerson.get());
        }else {
            throw new EntityNotFoundException("PersonWithId=" + id + "NotFound");
        }
    }

}
