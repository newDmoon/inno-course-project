package org.innowise.orderservice.mapper;

import org.innowise.orderservice.model.dto.OrderDTO;
import org.innowise.orderservice.model.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderDTO toDTO(Order order);

    @Mapping(target = "orderItems", ignore = true)
    Order toEntity(OrderDTO orderDTO);
}