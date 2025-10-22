package org.innowise.orderservice.service;

import org.innowise.orderservice.model.dto.OrderDTO;
import org.innowise.orderservice.model.dto.OrderFilterDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderDTO createOrder(OrderDTO orderDTO);
    OrderDTO getOrderById(Long id);
    Page<OrderDTO> getOrders(OrderFilterDTO filter, Pageable pageable);
    OrderDTO updateOrderById(Long id, OrderDTO orderDTO);
    boolean deleteOrderById(Long id);
}
