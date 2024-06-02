package com.adagency.dbwork.jparepo;

import com.adagency.model.entity.BaseModelPerson;
import com.adagency.model.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
	boolean existsByClientIdAndOrderStatusId(Long clientId, Long orderStatusId);
	List<Order> findByClientIdAndOrderStatusId(Long clientId, Long orderStatusId);
	
	
	@Query("SELECT p FROM Order p WHERE " +
			"(:workerId IS NULL OR p.worker.id = :workerId) AND " +
			"(:clientId IS NULL OR p.client.id = :clientId) AND " +
			"(:orderId IS NULL OR p.id = :orderId)")
	Page<Order> findByCriteria(
			@Param("workerId") Long workerId,
			@Param("clientId") Long clientId,
			@Param("orderId") Long orderId,
			Pageable pageable);


}
