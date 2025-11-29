package org.innowise.paymentservice.service.impl;

import org.innowise.paymentservice.client.RandomNumberClient;
import org.innowise.paymentservice.exception.AlreadyExistsException;
import org.innowise.paymentservice.mapper.PaymentMapper;
import org.innowise.paymentservice.model.PaymentStatus;
import org.innowise.paymentservice.model.dto.event.OrderCreatedEvent;
import org.innowise.paymentservice.model.dto.event.PaymentCreatedEvent;
import org.innowise.paymentservice.model.entity.Payment;
import org.innowise.paymentservice.repository.PaymentRepository;
import org.innowise.paymentservice.service.kafka.PaymentEventProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    private OrderCreatedEvent orderEvent;
    private Payment payment;
    private PaymentCreatedEvent paymentEvent;

    @BeforeEach
    void setup() {
        orderEvent = OrderCreatedEvent.builder()
                .orderId(1L)
                .userId(10L)
                .amount(BigDecimal.TEN)
                .build();

        payment = new Payment();
        payment.setOrderId(orderEvent.getOrderId());
        payment.setUserId(orderEvent.getUserId());
        payment.setPaymentAmount(orderEvent.getAmount());

        paymentEvent = PaymentCreatedEvent.builder()
                .orderId(orderEvent.getOrderId())
                .userId(orderEvent.getUserId())
                .amount(orderEvent.getAmount())
                .status(PaymentStatus.SUCCESS)
                .build();
    }

    @Test
    void processOrderEvent_WhenPaymentAlreadyExists_ShouldSkipProcessing() {
        when(paymentRepository.findByOrderId(orderEvent.getOrderId()))
                .thenReturn(Optional.of(new Payment()));

        paymentService.processOrderEvent(orderEvent);

        verify(paymentRepository, never()).save(any());
    }

    @Test
    void processOrderEvent_WhenPaymentDoesNotExist_ShouldSavePaymentAndSendEvent() throws Exception {
        when(paymentRepository.findByOrderId(orderEvent.getOrderId()))
                .thenReturn(Optional.empty());
        when(paymentMapper.toEntity(orderEvent)).thenReturn(payment);
        when(randomNumberClient.determinePaymentStatus()).thenReturn(PaymentStatus.SUCCESS);
        when(paymentMapper.toPaymentCreatedEvent(payment)).thenReturn(paymentEvent);

        paymentService.processOrderEvent(orderEvent);

        verify(paymentRepository).save(payment);
        verify(producer).sendPaymentEvent(paymentEvent);
        assertEquals(PaymentStatus.SUCCESS, payment.getStatus());
    }

    @Test
    void processOrderEvent_WhenProducerThrowsException_ShouldMarkPaymentFailed() throws Exception {
        when(paymentRepository.findByOrderId(orderEvent.getOrderId()))
                .thenReturn(Optional.empty());
        when(paymentMapper.toEntity(orderEvent)).thenReturn(payment);
        when(randomNumberClient.determinePaymentStatus()).thenReturn(PaymentStatus.SUCCESS);
        when(paymentMapper.toPaymentCreatedEvent(payment)).thenReturn(paymentEvent);

        doThrow(new RuntimeException("down")).when(producer).sendPaymentEvent(paymentEvent);

        paymentService.processOrderEvent(orderEvent);

        verify(paymentRepository, times(2)).save(payment);
        assertEquals(PaymentStatus.FAILED, payment.getStatus());
    }
}
