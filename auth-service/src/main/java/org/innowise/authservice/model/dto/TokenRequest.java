package org.innowise.authservice.model.dto;

import jakarta.validation.constraints.NotBlank;

public record TokenRequest(
        @NotBlank
        String token
) {
}
