package org.innowise.paymentservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.innowise.paymentservice.util.ApplicationConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfiguration {
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
}
