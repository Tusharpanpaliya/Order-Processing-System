package com.ops.orders.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.ops.orders.dto.CreateOrderRequest;
import com.ops.orders.dto.OrderResponse;
import com.ops.orders.event.OrderCreatedEvent;
import com.ops.orders.exception.BadRequestException;
import com.ops.orders.exception.NotFoundException;
import com.ops.orders.mapper.OrderMapper;
import com.ops.orders.model.Order;
import com.ops.orders.model.OrderStatus;
import com.ops.orders.repository.OrderRepository;

@Service
public class OrderService {
	private final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository repo;
    private final ApplicationEventPublisher publisher;
    
    public OrderService(OrderRepository repo, ApplicationEventPublisher publisher) {
        this.repo = repo;
        this.publisher = publisher;
    }

    public OrderResponse createOrder(CreateOrderRequest req) {
        Order order = OrderMapper.toEntity(req);
        repo.save(order);
        log.info("Created new order [{}] for customer {}", order.getId(), order.getCustomerName());
        publisher.publishEvent(new OrderCreatedEvent(order));
        return OrderMapper.toResponse(order);
    }

    public OrderResponse getById(String id) {
        Order order = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found: " + id));
        return OrderMapper.toResponse(order);
    }

    public List<OrderResponse> listAll(OrderStatus status) {
        List<Order> orders = (status == null) ? repo.findAll() : repo.findByStatus(status);
        return orders.stream().map(OrderMapper::toResponse).collect(Collectors.toList());
    }

    public OrderResponse cancelOrder(String id) {
        Order order = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found: " + id));
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BadRequestException("Order can only be cancelled if PENDING");
        }
        order.setStatus(OrderStatus.CANCELLED);
        repo.save(order);
        log.warn("Order {} cancelled", order.getId());
        return OrderMapper.toResponse(order);
    }
    
    public OrderResponse updateStatus(String id, OrderStatus newStatus) {
		Order order = repo.findById(id)
				.orElseThrow(() -> new NotFoundException("Order not found: " + id));
		if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException("Cannot update status of a cancelled order");
        }		
		if (order.getStatus() == newStatus) {
            log.info("Order {} already has status {}, skipping update", id, newStatus);
            return OrderMapper.toResponse(order);
        }
		order.setStatus(newStatus);
		repo.save(order);
		log.info("Order {} status updated to {}", id, newStatus);
		return OrderMapper.toResponse(order);
	}
}