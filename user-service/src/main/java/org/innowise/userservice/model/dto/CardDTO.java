package org.innowise.userservice.model.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CardDTO(
        Long id,
        @NotNull(message = "User ID cannot be null")
        Long userId,
        @NotNull(message = "Card number cannot be null")
        @Size(min = 13, max = 19, message = "Card number must be between 13 and 19 digits")
        @Pattern(regexp = "^[0-9]+$", message = "Card number must contain only digits")
        @Size(max = 30)
        String number,
        @NotNull(message = "Card holder name cannot be null")
        @Size(min = 2, max = 100, message = "Card holder name must be between 2 and 100 characters")
        @Size(max = 100)
        String holder,
        @NotNull(message = "Expiration date cannot be null")
        @FutureOrPresent(message = "Expiration date must be in the future")
        LocalDate expirationDate
) {}
