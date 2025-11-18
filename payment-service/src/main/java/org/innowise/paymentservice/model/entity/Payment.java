package org.innowise.paymentservice.model.entity;

import lombok.Data;
import org.innowise.paymentservice.model.PaymentStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Document
public class Payment {
    @Id
    private String id;

    @Indexed
    private Long orderId;

    @Indexed
    private Long userId;

    private PaymentStatus status;
    private Instant timestamp = Instant.now();
    private BigDecimal paymentAmount;
}
