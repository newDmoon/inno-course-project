package org.innowise.userservice.model.dto;

import java.time.LocalDate;

public record CardResponse(
        String number,
        String holder,
        LocalDate expirationDate
) {}
