package org.innowise.paymentservice.service.impl;

import com.mongodb.DuplicateKeyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.innowise.paymentservice.client.RandomNumberClient;
import org.innowise.paymentservice.exception.AlreadyExistsException;
import org.innowise.paymentservice.mapper.PaymentMapper;
import org.innowise.paymentservice.model.PaymentStatus;
import org.innowise.paymentservice.model.dto.event.OrderCreatedEvent;
import org.innowise.paymentservice.model.dto.event.PaymentCreatedEvent;
import org.innowise.paymentservice.model.entity.Payment;
import org.innowise.paymentservice.repository.PaymentRepository;
import org.innowise.paymentservice.service.PaymentService;
import org.innowise.paymentservice.service.kafka.PaymentEventProducer;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomPaymentService implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final RandomNumberClient randomNumberClient;
    private final PaymentEventProducer producer;

    public void processOrderEvent(OrderCreatedEvent event) {
        Optional<Payment> existingPaymentOpt = paymentRepository.findByOrderId(event.getOrderId());

        if (existingPaymentOpt.isPresent()) {
            log.info("Payment for order {} already exists. Skipping processing.", event.getOrderId());
            return;
        }

        Payment payment = paymentMapper.toEntity(event);
        processAndSave(payment);

        PaymentCreatedEvent responseEvent = paymentMapper.toPaymentCreatedEvent(payment);

        try {
            producer.sendPaymentEvent(responseEvent);
        } catch (Exception ex) {
            log.error("Failed to send payment event for order {}. Marking payment as FAILED.", payment.getOrderId(), ex);
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
        }

        log.info("Processed payment for order {}", payment.getOrderId());
    }

    private void processAndSave(Payment payment) {
        payment.setStatus(randomNumberClient.determinePaymentStatus());
        payment.setTimestamp(Instant.now());

        try {
            paymentRepository.save(payment);
        } catch (DuplicateKeyException ex) {
            throw new AlreadyExistsException(payment.getOrderId());
        }

        log.info("Payment processed: {} with status {}", payment.getOrderId(), payment.getStatus());
    }
}
