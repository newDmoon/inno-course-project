package org.innowise.orderservice.service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.innowise.orderservice.exception.NotFoundException;
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

        if (event == null || event.orderId() == null || event.paymentStatus() == null) {
            log.error("Invalid event received: {}", event);
            throw new NotFoundException("Invalid PaymentCreatedEvent");
        }

        try {
            orderService.updateOrderStatusFromPayment(event);
        } catch (Exception ex) {
            log.error("Error processing PaymentCreatedEvent: {}", event, ex);
            throw ex;
        }
    }
}
