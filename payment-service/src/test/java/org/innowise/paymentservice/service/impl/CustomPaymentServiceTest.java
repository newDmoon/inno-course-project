package org.innowise.paymentservice.service.impl;

import org.innowise.paymentservice.client.RandomNumberClient;
import org.innowise.paymentservice.mapper.PaymentMapper;
import org.innowise.paymentservice.model.PaymentStatus;
import org.innowise.paymentservice.model.dto.event.OrderCreatedEvent;
import org.innowise.paymentservice.model.dto.event.PaymentCreatedEvent;
import org.innowise.paymentservice.model.entity.Payment;
import org.innowise.paymentservice.repository.PaymentRepository;
import org.innowise.paymentservice.service.kafka.PaymentEventProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CustomPaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private RandomNumberClient randomNumberClient;

    @Mock
    private PaymentEventProducer producer;

    @InjectMocks
    private CustomPaymentService paymentService;

    @Test
    void testDetermineStatus_ReturnsSuccess() {
        Mockito.when(randomNumberClient.getRandomInt(1, 100)).thenReturn(42);

        PaymentStatus status = paymentService.determineStatus();

        assertEquals(PaymentStatus.SUCCESS, status);
    }

    @Test
    void testDetermineStatus_ReturnsFailed() {
        Mockito.when(randomNumberClient.getRandomInt(1, 100)).thenReturn(41);

        PaymentStatus status = paymentService.determineStatus();

        assertEquals(PaymentStatus.FAILED, status);
    }

    @Test
    void testProcessOrderEvent() {
        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(1L)
                .userId(10L)
                .amount(BigDecimal.ONE)
                .build();

        Payment payment = new Payment();
        payment.setOrderId(1L);
        payment.setUserId(10L);
        payment.setPaymentAmount(BigDecimal.ONE);

        Mockito.when(paymentMapper.toEntity(event)).thenReturn(payment);

        Mockito.when(randomNumberClient.getRandomInt(1, 100)).thenReturn(2);

        PaymentCreatedEvent mappedEvent = PaymentCreatedEvent.builder()
                .orderId(1L)
                .userId(10L)
                .amount(BigDecimal.ONE)
                .status(PaymentStatus.SUCCESS)
                .timestamp(payment.getTimestamp())
                .build();

        Mockito.when(paymentMapper.toPaymentCreatedEvent(payment)).thenReturn(mappedEvent);

        paymentService.processOrderEvent(event);

        Mockito.verify(paymentRepository).save(payment);
        Mockito.verify(producer).sendPaymentEvent(mappedEvent);

        assertEquals(PaymentStatus.SUCCESS, payment.getStatus());
    }
}
