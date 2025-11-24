package org.innowise.orderservice.service;

import org.innowise.orderservice.model.dto.OrderDTO;
import org.innowise.orderservice.model.dto.UserDTO;

import java.util.List;

public interface OrderEnrichmentService {
    OrderDTO enrichWithUser(OrderDTO orderDTO);

    List<OrderDTO> enrichWithUsers(List<OrderDTO> orderDTO);

    OrderDTO enrichOrder(OrderDTO order, UserDTO userDTO);
}