package com.adagency.dbwork.jparepo;

import com.adagency.model.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatusRepository extends JpaRepository<Status, Long> {

	Optional<Status> findByName(String name);
}
