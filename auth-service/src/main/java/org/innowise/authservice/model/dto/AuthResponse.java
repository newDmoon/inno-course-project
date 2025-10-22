package org.innowise.authservice.model.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken
) {
}
