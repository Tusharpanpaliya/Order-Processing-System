package com.ops.orders.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ops.orders.dto.CreateOrderRequest;
import com.ops.orders.dto.OrderResponse;
import com.ops.orders.dto.UpdateStatusRequest;
import com.ops.orders.model.OrderStatus;
import com.ops.orders.service.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService svc;

    public OrderController(OrderService svc) {
        this.svc = svc;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@Valid @RequestBody CreateOrderRequest req) {
        return svc.createOrder(req);
    }

    @GetMapping("/{id}")
    public OrderResponse getOrder(@PathVariable String id) {
        return svc.getById(id);
    }

    @GetMapping
    public List<OrderResponse> listOrders(@RequestParam(name = "status", required = false) OrderStatus status) {
        return svc.listAll(status);
    }

    @PatchMapping("/{id}/status")
    public OrderResponse updateStatus(@PathVariable String id, @Valid @RequestBody UpdateStatusRequest req) {
        return svc.updateStatus(id, req.getStatus());
    }

    @PostMapping("/{id}/cancel")
    public OrderResponse cancelOrder(@PathVariable String id) {
        return svc.cancelOrder(id);
    }
}