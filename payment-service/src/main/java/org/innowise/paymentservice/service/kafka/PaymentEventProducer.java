package org.innowise.paymentservice.service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.innowise.paymentservice.model.dto.event.PaymentCreatedEvent;
import org.innowise.paymentservice.util.ApplicationConstant;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentEventProducer {
    private final KafkaTemplate<String, PaymentCreatedEvent> kafkaTemplate;

    public void sendPaymentEvent(PaymentCreatedEvent event) {
        log.info("Sending CREATE_PAYMENT event: {}", event);
        kafkaTemplate.send(ApplicationConstant.TOPIC_CREATE_PAYMENT, event);
    }
}