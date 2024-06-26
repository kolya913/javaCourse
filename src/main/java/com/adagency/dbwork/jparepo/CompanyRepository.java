package com.adagency.dbwork.jparepo;

import com.adagency.model.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
	Optional<Company> findById(Long id);
}
