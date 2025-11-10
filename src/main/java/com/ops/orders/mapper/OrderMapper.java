package com.ops.orders.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.ops.orders.dto.CreateOrderRequest;
import com.ops.orders.dto.OrderItemResponse;
import com.ops.orders.dto.OrderResponse;
import com.ops.orders.model.Order;
import com.ops.orders.model.OrderItem;

public class OrderMapper {

	public static Order toEntity(CreateOrderRequest req) {
        Order order = new Order();
        order.setCustomerName(req.getCustomerName());

        List<OrderItem> items = Optional.ofNullable(req.getItems())
            .orElse(Collections.emptyList())
            .stream()
            .map(i -> {
                OrderItem item = new OrderItem();
                item.setProductId(i.getProductId());
                item.setName(i.getName());
                item.setQuantity(i.getQuantity());
                item.setPrice(i.getPrice());
                item.setOrder(order);
                return item;
            })
            .collect(Collectors.toList());

        order.setItems(items);
        order.calculateTotal();
        return order;
    }

    public static OrderResponse toResponse(Order o) {
        OrderResponse res = new OrderResponse();
        res.setId(o.getId());
        res.setCustomerName(o.getCustomerName());
        res.setStatus(o.getStatus());
        res.setCreatedAt(o.getCreatedAt());
        res.setTotalAmount(o.getTotalAmount());

        List<OrderItemResponse> itemResponses = Optional.ofNullable(o.getItems())
            .orElse(Collections.emptyList())
            .stream()
            .map(i -> {
                OrderItemResponse ir = new OrderItemResponse();
                ir.setProductId(i.getProductId());
                ir.setName(i.getName());
                ir.setQuantity(i.getQuantity());
                ir.setPrice(i.getPrice());
                ir.setTotal(i.getTotal());
                return ir;
            })
            .collect(Collectors.toList());

        res.setItems(itemResponses);
        return res;
    }
}