package org.innowise.paymentservice.model.dto;

public record ValidationError(
        String field,
        Object rejectedValue,
        String message,
        String errorCode
) { }