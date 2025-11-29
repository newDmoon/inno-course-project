package org.innowise.paymentservice.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.innowise.paymentservice.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentDTO(
        Long id,
        @NotNull(message = "Order ID must not be null")
        @Positive
        Long orderId,
        @NotNull(message = "User ID must not be null")
        @Positive
        Long userId,
        PaymentStatus status,
        Instant timestamp,
        @NotNull(message = "Payment amount cannot be null")
        @Positive(message = "Payment amount must be positive")
        BigDecimal paymentAmount) {
}
