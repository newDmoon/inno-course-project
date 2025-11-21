package org.innowise.paymentservice.service;

import org.innowise.paymentservice.model.dto.event.OrderCreatedEvent;

public interface PaymentService {
    void processOrderEvent(OrderCreatedEvent event);
}
