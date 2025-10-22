package org.innowise.orderservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.innowise.orderservice.exception.NotFoundException;
import org.innowise.orderservice.mapper.OrderMapper;
import org.innowise.orderservice.model.OrderStatus;
import org.innowise.orderservice.model.dto.OrderDTO;
import org.innowise.orderservice.model.dto.OrderFilterDTO;
import org.innowise.orderservice.model.entity.Order;
import org.innowise.orderservice.repository.OrderRepository;
import org.innowise.orderservice.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOrderService implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {
        Order order = orderMapper.toEntity(orderDTO);
        order.setCreationDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        Order savedOrder = orderRepository.save(order);
        return orderMapper.toDTO(savedOrder);
    }

    @Override
    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));
        return orderMapper.toDTO(order);
    }

    @Override
    public Page<OrderDTO> getOrders(OrderFilterDTO filter, Pageable pageable) {
        Page<Order> ordersPage;

        if (filter.ids() != null && !filter.ids().isEmpty()) {
            ordersPage = orderRepository.findAllByIdIn(filter.ids(), pageable);
        } else if (filter.statuses() != null && !filter.statuses().isEmpty()) {
            ordersPage = orderRepository.findAllByStatusIn(filter.statuses(), pageable);
        } else {
            ordersPage = orderRepository.findAll(pageable);
        }

        return ordersPage.map(orderMapper::toDTO);
    }

    @Override
    @Transactional
    public OrderDTO updateOrderById(Long id, OrderDTO orderDTO) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));
        
        existingOrder.setUserId(orderDTO.userId());
        existingOrder.setStatus(orderDTO.status());
        
        Order savedOrder = orderRepository.save(existingOrder);
        return orderMapper.toDTO(savedOrder);
    }

    @Override
    @Transactional
    public boolean deleteOrderById(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new NotFoundException(id);
        }
        
        orderRepository.deleteById(id);
        return true;
    }
}
