package org.innowise.orderservice.service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.innowise.orderservice.model.dto.PaymentCreatedEvent;
import org.innowise.orderservice.service.OrderService;
import org.innowise.orderservice.util.ApplicationConstant;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentEventConsumer {
    private final OrderService orderService;

    @KafkaListener(topics = ApplicationConstant.TOPIC_CREATE_PAYMENT)
    public void handlePaymentCreatedEvent(PaymentCreatedEvent event) {
        log.info("Received PaymentCreatedEvent: {}", event);
        orderService.updateOrderStatusFromPayment(event);
    }
}
