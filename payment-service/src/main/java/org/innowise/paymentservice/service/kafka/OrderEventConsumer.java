package org.innowise.paymentservice.service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.innowise.paymentservice.exception.AlreadyExistsException;
import org.innowise.paymentservice.model.dto.event.OrderCreatedEvent;
import org.innowise.paymentservice.service.PaymentService;
import org.innowise.paymentservice.util.ApplicationConstant;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {
    private final PaymentService paymentService;
    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    @KafkaListener(topics = ApplicationConstant.TOPIC_CREATE_ORDER)
    public void handleOrderEvent(OrderCreatedEvent event) {
        log.info("Received CREATE_ORDER event: {}", event);

        try {
            paymentService.processOrderEvent(event);
        } catch (AlreadyExistsException ex) {
            log.info("Payment for order {} already processed, skipping.", event.getOrderId());
        } catch (Exception ex) {
            log.error("Failed to process order {}. Sending to DLQ or alerting system.", event.getOrderId(), ex);
            sendToDLQ(event);
        }
    }

    private void sendToDLQ(OrderCreatedEvent event) {
        kafkaTemplate.send(ApplicationConstant.TOPIC_CREATE_ORDER_DLQ, String.valueOf(event.getOrderId()), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send OrderCreatedEvent to DLQ: {}", event, ex);
                    } else {
                        log.info("OrderCreatedEvent {} sent to DLQ successfully.", event.getOrderId());
                    }
                });
    }
}
