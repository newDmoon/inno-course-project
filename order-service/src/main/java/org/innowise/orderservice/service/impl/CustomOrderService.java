package org.innowise.orderservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.innowise.orderservice.client.UserClient;
import org.innowise.orderservice.exception.NotFoundException;
import org.innowise.orderservice.mapper.OrderMapper;
import org.innowise.orderservice.model.OrderStatus;
import org.innowise.orderservice.model.dto.OrderDTO;
import org.innowise.orderservice.model.dto.OrderFilterDTO;
import org.innowise.orderservice.model.dto.UserDTO;
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
    private final UserClient userClient;

    @Override
    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {
        Order order = orderMapper.toEntity(orderDTO);
        order.setCreationDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        Order savedOrder = orderRepository.save(order);
        OrderDTO response = orderMapper.toDTO(savedOrder);

        UserDTO userDTO = getUserInfo(response.userId());
        return enrichOrderWithUser(response, userDTO);
    }

    @Override
    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));


        OrderDTO orderDTO = orderMapper.toDTO(order);
        UserDTO userDTO = getUserInfo(orderDTO.userId());

        return enrichOrderWithUser(orderDTO, userDTO);
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

        return ordersPage.map(order -> {
            OrderDTO orderDTO = orderMapper.toDTO(order);
            UserDTO userDTO = getUserInfo(orderDTO.userId());
            return enrichOrderWithUser(orderDTO, userDTO);
        });
    }

    @Override
    @Transactional
    public OrderDTO updateOrderById(Long id, OrderDTO orderDTO) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));
        
        existingOrder.setUserId(orderDTO.userId());
        existingOrder.setStatus(orderDTO.status());

        Order savedOrder = orderRepository.save(existingOrder);
        OrderDTO response = orderMapper.toDTO(savedOrder);

        UserDTO userDTO = getUserInfo(response.userId());
        return enrichOrderWithUser(response, userDTO);
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

    private UserDTO getUserInfo(Long id) {
        try {
            return userClient.getUserById(id);
        } catch (Exception e) {
            log.error("Failed to fetch user info for email={}: {}", id, e.getMessage());
            return null;
        }
    }

    private OrderDTO enrichOrderWithUser(OrderDTO orderDTO, UserDTO userDTO) {
        return OrderDTO.builder()
                .id(orderDTO.id())
                .userId(orderDTO.userId())
                .status(orderDTO.status())
                .creationDate(orderDTO.creationDate())
                .userDTO(userDTO)
                .build();
    }
}
