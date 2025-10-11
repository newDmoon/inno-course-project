package org.innowise.authservice.model.dto;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

public record ApiError(
        String message,
        int status,
        String error,
        String path,
        LocalDateTime timestamp,
        List<ValidationError> validationErrors,
        String errorCode
) {
    public ApiError {
        validationErrors = validationErrors != null ? validationErrors : List.of();
    }

    public static ApiError of(String message, HttpStatus status, String path) {
        return new ApiError(
                message,
                status.value(),
                status.getReasonPhrase(),
                path,
                LocalDateTime.now(),
                List.of(),
                null
        );
    }

    public static ApiError of(String message, HttpStatus status, String path, String errorCode) {
        return new ApiError(
                message,
                status.value(),
                status.getReasonPhrase(),
                path,
                LocalDateTime.now(),
                List.of(),
                errorCode
        );
    }

    public static ApiError withValidationErrors(
            String message,
            HttpStatus status,
            String path,
            List<ValidationError> validationErrors) {
        return new ApiError(
                message,
                status.value(),
                status.getReasonPhrase(),
                path,
                LocalDateTime.now(),
                validationErrors,
                null
        );
    }
}