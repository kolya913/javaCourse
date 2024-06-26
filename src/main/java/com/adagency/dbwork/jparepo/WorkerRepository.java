package com.adagency.dbwork.jparepo;

import com.adagency.model.entity.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkerRepository extends JpaRepository<Worker, Long> {
        Optional<Worker> findByEmail(String email);
        Optional<Worker> findById(Long id);
}
