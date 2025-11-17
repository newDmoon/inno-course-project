package org.innowise.paymentservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.innowise.paymentservice.client.RandomNumberClient;
import org.innowise.paymentservice.kafka.PaymentEventProducer;
import org.innowise.paymentservice.mapper.PaymentMapper;
import org.innowise.paymentservice.model.PaymentStatus;
import org.innowise.paymentservice.model.dto.event.OrderCreatedEvent;
import org.innowise.paymentservice.model.dto.event.PaymentCreatedEvent;
import org.innowise.paymentservice.model.entity.Payment;
import org.innowise.paymentservice.repository.PaymentRepository;
import org.innowise.paymentservice.service.PaymentService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomPaymentService implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final RandomNumberClient randomNumberClient;
    private final PaymentEventProducer producer;

    public void processOrderEvent(OrderCreatedEvent event) {
        Payment payment = paymentMapper.toEntity(event);
        payment.setStatus(PaymentStatus.PENDING);
        processAndSave(payment);
        PaymentCreatedEvent responseEvent = paymentMapper.toPaymentCreatedEvent(payment);

        producer.sendPaymentEvent(responseEvent);

        log.info("Processed payment for order {}", payment.getOrderId());
    }

    private void processAndSave(Payment payment) {
        payment.setStatus(determineStatus());
        paymentRepository.save(payment);

        log.info("Payment processed: {} with status {}", payment.getOrderId(), payment.getStatus());
    }

    private PaymentStatus determineStatus() {
        int number = randomNumberClient.getRandomInt(1, 100);
        return (number % 2 == 0) ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;
    }
}
