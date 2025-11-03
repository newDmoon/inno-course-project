package org.innowise.authservice.service;

import org.innowise.authservice.model.dto.AuthRequest;
import org.innowise.authservice.model.dto.AuthResponse;
import org.innowise.authservice.model.dto.RegistrationRequest;
import org.innowise.authservice.model.dto.TokenRequest;

/**
 * Service interface for handling authentication operations including login, registration,
 * token validation, and token refresh functionality.
 *
 * @author Dmitry Novogrodsky
 * @version 1.0
 */
public interface AuthService {
    /**
     * Authenticates a user with the provided credentials and returns authentication tokens.
     *
     * @param authRequest the authentication request containing user credentials (email and password)
     * @return AuthResponse containing access token, refresh token
     */
    AuthResponse login(AuthRequest authRequest);

    /**
     * Validates the provided token to ensure it is active and properly signed.
     *
     * @param tokenRequest the token request containing the token to validate
     * @return true if the token is valid and not expired, false otherwise
     */
    boolean validate(TokenRequest tokenRequest);

    /**
     * Refreshes the authentication tokens using a valid refresh token.
     *
     * @param tokenRequest the token request containing a valid refresh token
     * @return AuthResponse containing new access token and refresh token
     */
    AuthResponse refresh(TokenRequest tokenRequest);

    /**
     * Registers a new user with the provided credentials and returns authentication tokens.
     *
     * @param registrationRequest the authentication request containing user registration details
     * @return AuthResponse containing access token, refresh token
     */
    AuthResponse register(RegistrationRequest registrationRequest);
}
