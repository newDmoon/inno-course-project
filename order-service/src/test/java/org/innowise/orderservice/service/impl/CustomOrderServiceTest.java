package org.innowise.orderservice.service.impl;

import org.innowise.orderservice.exception.NotFoundException;
import org.innowise.orderservice.mapper.OrderMapper;
import org.innowise.orderservice.model.OrderStatus;
import org.innowise.orderservice.model.dto.OrderDTO;
import org.innowise.orderservice.model.dto.OrderFilterDTO;
import org.innowise.orderservice.model.entity.Order;
import org.innowise.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomOrderServiceTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private CustomOrderService orderService;

    private Long orderId;
    private Long userId;
    private LocalDateTime creationDate;
    private Order orderEntity;
    private OrderDTO orderDTO;

    @BeforeEach
    void setUp() {
        orderId = 1L;
        userId = 100L;
        creationDate = LocalDateTime.now();
        orderEntity = createOrderEntity();
        orderDTO = new OrderDTO(orderId, userId, OrderStatus.PENDING, creationDate);
    }

    @Test
    void createOrder_ShouldCreateOrderSuccessfully() {
        OrderDTO inputDTO = new OrderDTO(null, userId, OrderStatus.PENDING, null);

        when(orderMapper.toEntity(inputDTO)).thenReturn(orderEntity);
        when(orderRepository.save(orderEntity)).thenReturn(orderEntity);
        when(orderMapper.toDTO(orderEntity)).thenReturn(orderDTO);

        OrderDTO result = orderService.createOrder(inputDTO);

        assertEquals(orderDTO, result);
        assertNotNull(orderEntity.getCreationDate());
        assertEquals(OrderStatus.PENDING, orderEntity.getStatus());
        verify(orderRepository).save(orderEntity);
    }

    @Test
    void getOrderById_WhenOrderExists_ShouldReturnOrder() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderEntity));
        when(orderMapper.toDTO(orderEntity)).thenReturn(orderDTO);

        OrderDTO result = orderService.getOrderById(orderId);

        assertEquals(orderDTO, result);
        verify(orderRepository).findById(orderId);
    }

    @Test
    void getOrderById_WhenOrderNotExists_ShouldThrowNotFoundException() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.getOrderById(orderId));
    }

    @Test
    void getOrders_WithIdsFilter_ShouldReturnFilteredOrders() {
        Pageable pageable = PageRequest.of(0, 10);
        OrderFilterDTO filter = new OrderFilterDTO(List.of(1L, 2L), null);
        Page<Order> orderPage = new PageImpl<>(List.of(orderEntity), pageable, 1);

        when(orderRepository.findAllByIdIn(anyList(), any(Pageable.class))).thenReturn(orderPage);
        when(orderMapper.toDTO(any(Order.class))).thenReturn(orderDTO);

        Page<OrderDTO> result = orderService.getOrders(filter, pageable);

        assertEquals(1, result.getContent().size());
        verify(orderRepository).findAllByIdIn(filter.ids(), pageable);
        verify(orderRepository, never()).findAllByStatusIn(any(), any());
        verify(orderRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void getOrders_WithStatusesFilter_ShouldReturnFilteredOrders() {
        Pageable pageable = PageRequest.of(0, 10);
        OrderFilterDTO filter = new OrderFilterDTO(null, List.of(OrderStatus.PENDING, OrderStatus.CONFIRMED));
        Page<Order> orderPage = new PageImpl<>(List.of(orderEntity), pageable, 1);

        when(orderRepository.findAllByStatusIn(anyList(), any(Pageable.class))).thenReturn(orderPage);
        when(orderMapper.toDTO(any(Order.class))).thenReturn(orderDTO);

        Page<OrderDTO> result = orderService.getOrders(filter, pageable);

        assertEquals(1, result.getContent().size());
        verify(orderRepository).findAllByStatusIn(filter.statuses(), pageable);
        verify(orderRepository, never()).findAllByIdIn(any(), any());
        verify(orderRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void getOrders_WithoutFilters_ShouldReturnAllOrders() {
        Pageable pageable = PageRequest.of(0, 10);
        OrderFilterDTO filter = new OrderFilterDTO(null, null);
        Page<Order> orderPage = new PageImpl<>(List.of(orderEntity), pageable, 1);

        when(orderRepository.findAll(any(Pageable.class))).thenReturn(orderPage);
        when(orderMapper.toDTO(any(Order.class))).thenReturn(orderDTO);

        Page<OrderDTO> result = orderService.getOrders(filter, pageable);

        assertEquals(1, result.getContent().size());
        verify(orderRepository).findAll(pageable);
        verify(orderRepository, never()).findAllByIdIn(any(), any());
        verify(orderRepository, never()).findAllByStatusIn(any(), any());
    }

    @Test
    void updateOrderById_WhenOrderExists_ShouldUpdateOrder() {
        OrderDTO updateDTO = new OrderDTO(orderId, userId, OrderStatus.CONFIRMED, creationDate);
        Order updatedOrder = createOrderEntity();
        updatedOrder.setStatus(OrderStatus.CONFIRMED);
        OrderDTO expectedDTO = new OrderDTO(orderId, userId, OrderStatus.CONFIRMED, creationDate);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderEntity));
        when(orderRepository.save(orderEntity)).thenReturn(updatedOrder);
        when(orderMapper.toDTO(updatedOrder)).thenReturn(expectedDTO);

        OrderDTO result = orderService.updateOrderById(orderId, updateDTO);

        assertEquals(expectedDTO, result);
        assertEquals(updateDTO.userId(), orderEntity.getUserId());
        assertEquals(updateDTO.status(), orderEntity.getStatus());
        verify(orderRepository).save(orderEntity);
    }

    @Test
    void updateOrderById_WhenOrderNotExists_ShouldThrowNotFoundException() {
        OrderDTO updateDTO = new OrderDTO(orderId, userId, OrderStatus.CONFIRMED, creationDate);
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.updateOrderById(orderId, updateDTO));
    }

    @Test
    void deleteOrderById_WhenOrderExists_ShouldDeleteOrder() {
        when(orderRepository.existsById(orderId)).thenReturn(true);

        boolean result = orderService.deleteOrderById(orderId);

        assertTrue(result);
        verify(orderRepository).deleteById(orderId);
    }

    @Test
    void deleteOrderById_WhenOrderNotExists_ShouldThrowNotFoundException() {
        when(orderRepository.existsById(orderId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> orderService.deleteOrderById(orderId));

        verify(orderRepository, never()).deleteById(orderId);
    }

    private Order createOrderEntity() {
        Order order = new Order();
        order.setId(orderId);
        order.setUserId(userId);
        order.setStatus(OrderStatus.PENDING);
        order.setCreationDate(creationDate);
        return order;
    }
}