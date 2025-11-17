package org.innowise.paymentservice.model.dto;

import org.innowise.paymentservice.model.PaymentStatus;

import java.time.Instant;

public record PaymentDTO(Long id,
                         Long orderId,
                         Long userId,
                         PaymentStatus status,
                         Instant timestamp) {
}
