package org.innowise.orderservice.model.dto;

import org.innowise.orderservice.model.OrderStatus;

import java.util.List;

public record OrderFilterDTO(
        List<Long> ids,
        List<OrderStatus> statuses
) {
}
