package org.innowise.userservice.model.dto;

import java.time.LocalDate;

public record CardResponse(
        Long id,
        Long userId,
        String number,
        String holder,
        LocalDate expirationDate
) {}
