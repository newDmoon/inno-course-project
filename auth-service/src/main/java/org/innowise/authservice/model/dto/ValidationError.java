package org.innowise.authservice.model.dto;

public record ValidationError(
        String field,
        Object rejectedValue,
        String message,
        String errorCode
) {
}
