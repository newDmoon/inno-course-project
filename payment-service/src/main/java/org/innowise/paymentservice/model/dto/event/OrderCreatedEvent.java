package org.innowise.paymentservice.model.dto.event;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderCreatedEvent {
    private Long orderId;
    private Long userId;
    private BigDecimal amount;
}
