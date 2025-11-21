package org.innowise.paymentservice.mapper;

import org.innowise.paymentservice.model.dto.event.OrderCreatedEvent;
import org.innowise.paymentservice.model.dto.event.PaymentCreatedEvent;
import org.innowise.paymentservice.model.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    @Mapping(target = "paymentAmount", source = "amount")
    Payment toEntity(OrderCreatedEvent event);

    PaymentCreatedEvent toPaymentCreatedEvent(Payment payment);
}
