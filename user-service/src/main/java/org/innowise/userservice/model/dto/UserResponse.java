package org.innowise.userservice.model.dto;

import java.time.LocalDate;

public record UserResponse(
        String email,
        String name,
        String surname,
        LocalDate birthDate
) {}
