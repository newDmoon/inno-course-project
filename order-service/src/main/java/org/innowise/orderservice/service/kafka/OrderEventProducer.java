package org.innowise.orderservice.service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.innowise.orderservice.model.dto.OrderCreatedEvent;
import org.innowise.orderservice.util.ApplicationConstant;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventProducer {

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public void sendOrderCreatedEvent(OrderCreatedEvent event) {
        kafkaTemplate.send(ApplicationConstant.TOPIC_CREATE_ORDER, event.orderId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send OrderCreatedEvent: {}", event, ex);

                    } else {
                        log.info("OrderCreatedEvent sent successfully: topic={}, offset={}",
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
}
