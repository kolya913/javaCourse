package com.adagency.dbwork.jparepo;


import com.adagency.model.entity.BaseModelPerson;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface BaseModelPersonRepository extends JpaRepository<BaseModelPerson, Long> {
    
    Optional<BaseModelPerson> findByEmail(String email);

    List<BaseModelPerson>  findByEmailOrPhoneNumber(String email, String phoneNumber);

    List<BaseModelPerson> findAll();
    
    @Query("SELECT p FROM BaseModelPerson p WHERE " +
            "((:email IS NULL OR :email = '') OR p.email LIKE %:email%) AND " +
            "((:name IS NULL OR :name = '') OR p.name LIKE %:name%) AND " +
            "((:middleName IS NULL OR :middleName = '') OR p.middleName LIKE %:middleName%) AND " +
            "((:lastName IS NULL OR :lastName = '') OR p.lastName LIKE %:lastName%) AND " +
            "((:phoneNumber IS NULL OR :phoneNumber = '') OR p.phoneNumber LIKE %:phoneNumber%) AND " +
            "((p.inn IS NULL OR (:inn IS NULL OR :inn = '') OR p.inn LIKE %:inn%)) AND " +
            "((:client IS NULL OR :client = FALSE OR (:client = TRUE AND p.type = 'Client'))) AND " +
            "((:worker IS NULL OR :worker = FALSE OR (:worker = TRUE AND p.type = 'Worker'))) AND " +
            "((:deleteFlag IS NULL OR :deleteFlag = p.deleteFlag))")
    Page<BaseModelPerson> findByCriteria(
            @Param("email") String email,
            @Param("name") String name,
            @Param("middleName") String middleName,
            @Param("lastName") String lastName,
            @Param("phoneNumber") String phoneNumber,
            @Param("inn") String inn,
            @Param("client") Boolean client,
            @Param("worker") Boolean worker,
            @Param("deleteFlag") Boolean deleteFlag,
            Pageable pageable
    );
    
}
