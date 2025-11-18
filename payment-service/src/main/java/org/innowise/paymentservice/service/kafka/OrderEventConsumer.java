package org.innowise.paymentservice.service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.innowise.paymentservice.model.dto.event.OrderCreatedEvent;
import org.innowise.paymentservice.service.PaymentService;
import org.innowise.paymentservice.util.ApplicationConstant;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {
    private final PaymentService paymentService;

    @KafkaListener(topics = ApplicationConstant.TOPIC_CREATE_ORDER, groupId = "payment-service")
    public void handleOrderEvent(OrderCreatedEvent event) {
        log.info("Received CREATE_ORDER event: {}", event);
        paymentService.processOrderEvent(event);
    }
}
