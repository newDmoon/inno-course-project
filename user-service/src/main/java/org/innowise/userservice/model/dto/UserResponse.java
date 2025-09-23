package org.innowise.userservice.model.dto;

import java.time.LocalDate;

public record UserResponse(
        Long id,
        String email,
        String name,
        String surname,
        LocalDate birthDate
) {}
