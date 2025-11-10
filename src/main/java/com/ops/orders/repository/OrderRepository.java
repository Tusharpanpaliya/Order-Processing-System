package com.ops.orders.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ops.orders.model.Order;
import com.ops.orders.model.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
	List<Order> findByStatus(OrderStatus status);
}
