package org.innowise.orderservice.model.dto;

import java.math.BigDecimal;

public record OrderCreatedEvent(
        Long orderId,
        Long userId,
        BigDecimal amount
) {
}
