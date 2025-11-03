package org.innowise.authservice.model.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UserDTO(
        Long id,
        String email,
        String name,
        String surname,
        LocalDate birthDate
) {
}