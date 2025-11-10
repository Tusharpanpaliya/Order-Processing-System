package com.ops.orders.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.ops.orders.model.OrderStatus;

public class OrderResponse {
    private String id;
    private String customerName;
    private OrderStatus status;
    private Instant createdAt;
    private BigDecimal totalAmount;
    private List<OrderItemResponse> items;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt;}
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount;}
    
	public List<OrderItemResponse> getItems() { return items; }
	public void setItems(List<OrderItemResponse> items) { this.items = items; }
}