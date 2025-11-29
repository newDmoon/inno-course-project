package org.innowise.orderservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.innowise.orderservice.util.ApplicationConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConfiguration {
    private final int MAX_RETRY_ATTEMPTS = 3;
    private final long BACKOFF_INTERVAL = 2000L;
    private final String DLQ_PART = "_DLQ";

    @Bean
    public NewTopic createPaymentTopic() {
        return new NewTopic(
                ApplicationConstant.TOPIC_CREATE_PAYMENT,
                ApplicationConstant.PARTITION_COUNT,
                ApplicationConstant.REPLICATION_FACTOR);
    }

    @Bean
    public NewTopic createOrderTopic() {
        return new NewTopic(
                ApplicationConstant.TOPIC_CREATE_ORDER,
                ApplicationConstant.PARTITION_COUNT,
                ApplicationConstant.REPLICATION_FACTOR);
    }

    @Bean
    public NewTopic createPaymentDLQTopic() {
        return new NewTopic(
                ApplicationConstant.TOPIC_CREATE_PAYMENT_DLQ,
                ApplicationConstant.PARTITION_COUNT,
                ApplicationConstant.REPLICATION_FACTOR);
    }

    @Bean
    public NewTopic createOrderDLQTopic() {
        return new NewTopic(
                ApplicationConstant.TOPIC_CREATE_ORDER_DLQ,
                ApplicationConstant.PARTITION_COUNT,
                ApplicationConstant.REPLICATION_FACTOR);
    }

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<Object, Object> template) {

        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(template, (r, e) ->
                        new org.apache.kafka.common.TopicPartition(
                                r.topic() + DLQ_PART, r.partition()
                        )
                );

        FixedBackOff backOff = new FixedBackOff(BACKOFF_INTERVAL, MAX_RETRY_ATTEMPTS);

        return new DefaultErrorHandler(recoverer, backOff);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Object, Object> kafkaListenerContainerFactory(
            ConsumerFactory<Object, Object> consumerFactory,
            DefaultErrorHandler errorHandler) {

        ConcurrentKafkaListenerContainerFactory<Object, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }
}
