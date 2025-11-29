package org.innowise.paymentservice.model.dto.event;

import lombok.Builder;
import lombok.Data;
import org.innowise.paymentservice.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class PaymentCreatedEvent {
    private String id;
    private Long orderId;
    private Long userId;
    private PaymentStatus status;
    private BigDecimal amount;
    private Instant timestamp;
}
