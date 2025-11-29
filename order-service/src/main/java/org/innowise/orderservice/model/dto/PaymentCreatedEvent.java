package org.innowise.orderservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.innowise.orderservice.model.PaymentStatus;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCreatedEvent {
    private Long orderId;
    private PaymentStatus status;
}
