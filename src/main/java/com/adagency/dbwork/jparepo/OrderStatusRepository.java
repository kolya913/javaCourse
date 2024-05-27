package com.adagency.dbwork.jparepo;

import com.adagency.model.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderStatusRepository extends JpaRepository<OrderStatus, Long> {
	public Optional<OrderStatus> findByName(String name);
}
