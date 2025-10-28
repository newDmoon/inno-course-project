package org.innowise.orderservice.service;

import org.innowise.orderservice.model.dto.OrderDTO;

import java.util.List;

public interface OrderEnrichmentService {
    OrderDTO enrichWithUser(OrderDTO orderDTO);
    List<OrderDTO> enrichWithUsers(List<OrderDTO> orderDTO);
}