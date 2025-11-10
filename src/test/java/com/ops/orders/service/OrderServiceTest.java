package com.ops.orders.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import com.ops.orders.dto.CreateOrderRequest;
import com.ops.orders.dto.UpdateStatusRequest;
import com.ops.orders.event.OrderCreatedEvent;
import com.ops.orders.exception.BadRequestException;
import com.ops.orders.exception.NotFoundException;
import com.ops.orders.mapper.OrderMapper;
import com.ops.orders.model.Order;
import com.ops.orders.model.OrderStatus;
import com.ops.orders.repository.OrderRepository;

class OrderServiceTest {
	@Mock
    private OrderRepository repo;

    @Mock
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private OrderService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void createOrder_shouldSaveAndPublishEvent() {
        // given
        CreateOrderRequest req = new CreateOrderRequest();
        req.setCustomerName("Tushar");
        req.setItems(List.of());
        Order order = OrderMapper.toEntity(req);
        when(repo.save(any(Order.class))).thenReturn(order);

        // when
        var response = service.createOrder(req);

        // then
        assertEquals("Tushar", response.getCustomerName());
        verify(repo, times(1)).save(any(Order.class));
        verify(publisher, times(1)).publishEvent(any(OrderCreatedEvent.class));
    }

    @Test
    void cancelOrder_shouldCancelIfPending() {
        // given
        Order order = new Order();
        order.setId("O1");
        order.setStatus(OrderStatus.PENDING);
        when(repo.findById("O1")).thenReturn(Optional.of(order));

        // when
        var response = service.cancelOrder("O1");

        // then
        assertEquals(OrderStatus.CANCELLED, response.getStatus());
        verify(repo, times(1)).save(order);
    }

    @Test
    void cancelOrder_shouldThrowIfNotPending() {
        Order order = new Order();
        order.setId("O2");
        order.setStatus(OrderStatus.SHIPPED);
        when(repo.findById("O2")).thenReturn(Optional.of(order));

        assertThrows(BadRequestException.class, () -> service.cancelOrder("O2"));
    }

    @Test
    void updateStatus_shouldChangeStatus() {
        Order order = new Order();
        order.setId("O3");
        order.setStatus(OrderStatus.PENDING);
        when(repo.findById("O3")).thenReturn(Optional.of(order));

        UpdateStatusRequest req = new UpdateStatusRequest();
        req.setStatus(OrderStatus.SHIPPED);

        var response = service.updateStatus("O3", req.getStatus());

        assertEquals(OrderStatus.SHIPPED, response.getStatus());
        verify(repo, times(1)).save(order);
    }

    @Test
    void updateStatus_shouldThrowIfCancelledOrder() {
        Order order = new Order();
        order.setId("O4");
        order.setStatus(OrderStatus.CANCELLED);
        when(repo.findById("O4")).thenReturn(Optional.of(order));

        UpdateStatusRequest req = new UpdateStatusRequest();
        req.setStatus(OrderStatus.SHIPPED);

        assertThrows(BadRequestException.class, () -> service.updateStatus("O4", req.getStatus()));
    }

    @Test
    void updateStatus_shouldSkipIfSameStatus() {
        Order order = new Order();
        order.setId("O5");
        order.setStatus(OrderStatus.SHIPPED);
        when(repo.findById("O5")).thenReturn(Optional.of(order));

        UpdateStatusRequest req = new UpdateStatusRequest();
        req.setStatus(OrderStatus.SHIPPED);

        var response = service.updateStatus("O5", req.getStatus());

        assertEquals(OrderStatus.SHIPPED, response.getStatus());
        verify(repo, never()).save(any());
    }

    @Test
    void getById_shouldThrowIfNotFound() {
        when(repo.findById("X")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getById("X"));
    }

    @Test
    void listAll_shouldReturnAllOrders() {
        Order order1 = new Order();
        order1.setId("O1");
        Order order2 = new Order();
        order2.setId("O2");

        when(repo.findAll()).thenReturn(List.of(order1, order2));

        var result = service.listAll(null);

        assertEquals(2, result.size());
        verify(repo, times(1)).findAll();
    }
}