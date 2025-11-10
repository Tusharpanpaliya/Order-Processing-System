package com.ops.orders.event;

import org.springframework.context.ApplicationEvent;

import com.ops.orders.model.Order;

public class OrderCreatedEvent extends ApplicationEvent {
    private static final long serialVersionUID = 1L;
	
    public OrderCreatedEvent(Order order) {
        super(order);
    }
    
    public Order getOrder() { return (Order) getSource(); }
}