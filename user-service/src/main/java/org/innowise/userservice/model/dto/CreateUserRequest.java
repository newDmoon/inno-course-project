package org.innowise.userservice.model.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateUserRequest(
        @NotNull @NotEmpty
        @Size(min = 10, max = 100)
        String email,
        @Size(max = 100)
        String name,
        @Size(max = 100)
        String surname,
        LocalDate birthDate) {
}
