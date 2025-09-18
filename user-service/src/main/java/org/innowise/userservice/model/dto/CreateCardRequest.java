package org.innowise.userservice.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateCardRequest(
        @NotNull
        Long userId,
        @Size(max = 30)
        String number,
        @Size(max = 100)
        String holder,
        LocalDate expirationDate
) {}
