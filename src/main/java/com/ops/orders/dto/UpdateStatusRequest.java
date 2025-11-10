package com.ops.orders.dto;

import com.ops.orders.model.OrderStatus;

import jakarta.validation.constraints.NotNull;

public class UpdateStatusRequest {
	@NotNull
    private OrderStatus status;

    public UpdateStatusRequest() {}

    public UpdateStatusRequest(OrderStatus status) {
        this.status = status;
    }
    
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
}
