package org.innowise.paymentservice.repository;

import org.innowise.paymentservice.model.PaymentStatus;
import org.innowise.paymentservice.model.entity.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {
    List<Payment> findByOrderId(Long orderId);

    List<Payment> findByUserId(Long userId);

    List<Payment> findByStatusIn(List<PaymentStatus> statuses);

    List<Payment> findByTimestampBetween(Instant start, Instant end);
}
