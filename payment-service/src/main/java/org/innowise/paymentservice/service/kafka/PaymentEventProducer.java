package org.innowise.paymentservice.service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.innowise.paymentservice.model.dto.event.PaymentCreatedEvent;
import org.innowise.paymentservice.util.ApplicationConstant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentEventProducer {
    private final KafkaTemplate<String, PaymentCreatedEvent> kafkaTemplate;

    @Value("${external.kafka.timeout}")
    private int timeout;

    @Retryable(
            value = {Exception.class},
            maxAttemptsExpression = "${external.kafka.retry-attempts}",
            backoff = @Backoff(delayExpression = "${external.kafka.retry-delay}")
    )
    public void sendPaymentEvent(PaymentCreatedEvent event) throws ExecutionException, InterruptedException, TimeoutException {
        log.info("Sending CREATE_PAYMENT event: {}", event);

        try {
            SendResult<String, PaymentCreatedEvent> result = kafkaTemplate
                    .send(ApplicationConstant.TOPIC_CREATE_PAYMENT, event.getId(), event)
                    .get(timeout, TimeUnit.MILLISECONDS);

            log.info("Event sent successfully to topic={} partition={} offset={}",
                    result.getRecordMetadata().topic(),
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset());
        } catch (Exception ex) {
            log.warn("Attempt failed for event {}. Retrying ", event, ex);
            throw ex;
        }
    }

    @Recover
    public void sendToDLQ(Exception ex, PaymentCreatedEvent event) {
        log.error("Failed to send event {} after retries. Moving to Dead Letter Queue (DLQ)", event, ex);

        kafkaTemplate.send(ApplicationConstant.TOPIC_CREATE_PAYMENT_DLQ, event.getId(), event)
                .whenComplete((result, dlqEx) -> {
                    if (dlqEx != null) {
                        log.error("Failed to send event {} to DLQ!", event, dlqEx);
                    } else {
                        log.info("Event {} sent to DLQ successfully.", event);
                    }
                });
    }
}