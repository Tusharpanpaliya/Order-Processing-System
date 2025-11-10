package com.ops.orders.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.ops.orders.event.OrderCreatedEvent;
import com.ops.orders.model.Order;
import com.ops.orders.model.OrderStatus;
import com.ops.orders.repository.OrderRepository;

@Component
public class OrderEventListener {
	private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class);
    private final OrderRepository repo;
    
    public OrderEventListener(OrderRepository repo) {
        this.repo = repo;
    }
    
    @Async
    @EventListener
    public void handleOrderCreatedEvent(OrderCreatedEvent event) throws InterruptedException {
    	Order order = event.getOrder();
    	log.info("Received event for new order: {}", order.getId());
    	
    	Thread.sleep(5000); // pretend some background work happens
        order.setStatus(OrderStatus.PROCESSING);
        repo.save(order);
        log.info("Order {} moved to PROCESSING (via event)", order.getId());
    }
}
