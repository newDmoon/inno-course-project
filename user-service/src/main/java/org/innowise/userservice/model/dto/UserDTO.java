package org.innowise.userservice.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record UserDTO(
        Long id,
        @NotNull(message = "Email is required")
        @NotEmpty(message = "Email cannot be empty")
        @Size(min = 10, max = 100, message = "Email must be between 10 and 100 characters")
        @Email(message = "Email should be valid")
        String email,
        @Size(max = 100, message = "Name cannot exceed 100 characters")
        String name,
        @Size(max = 100, message = "Surname cannot exceed 100 characters")
        String surname,
        LocalDate birthDate,
        List<CardDTO> cards
) {}
