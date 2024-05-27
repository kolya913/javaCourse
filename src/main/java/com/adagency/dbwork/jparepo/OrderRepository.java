package com.adagency.dbwork.jparepo;

import com.adagency.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
	boolean existsByClientIdAndOrderStatusId(Long clientId, Long orderStatusId);
	List<Order> findByClientIdAndOrderStatusId(Long clientId, Long orderStatusId);
}
