package org.innowise.authservice.model.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record RegistrationRequest(
        @Size(min = 4, max = 30)
        @Email
        String email,
        @Size(min = 4, max = 30)
        String password,
        @NotBlank
        String name,
        @NotBlank
        String surname,
        @NotNull
        @Past
        LocalDate birthDate
) {
}