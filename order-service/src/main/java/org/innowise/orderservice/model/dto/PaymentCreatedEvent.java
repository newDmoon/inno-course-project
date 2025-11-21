package org.innowise.orderservice.model.dto;

import org.innowise.orderservice.model.PaymentStatus;

public record PaymentCreatedEvent(
        Long orderId,
        PaymentStatus paymentStatus
) {
}
