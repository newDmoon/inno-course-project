package org.innowise.authservice.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRequest(
        @NotBlank
        @Size(min = 4, max = 30)
        @Email
        String email,
        @NotBlank
        @Size(min = 4, max = 30)
        String password
) {
}
