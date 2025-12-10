package org.innowise.authservice.controller;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.innowise.authservice.exception.AlreadyExistsException;
import org.innowise.authservice.model.dto.AuthRequest;
import org.innowise.authservice.model.dto.AuthResponse;
import org.innowise.authservice.model.dto.RegistrationRequest;
import org.innowise.authservice.model.dto.TokenRequest;
import org.innowise.authservice.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for handling authentication operations.
 * Provides endpoints for user registration, login, token validation, and token refresh.
 *
 * @author Dmitry Novogrodsky
 * @version 1.0
 */
@RestController
@CrossOrigin(origins = "${services.front.url:http://localhost:3000}")
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    /**
     * Authenticates a user with provided credentials and returns authentication tokens.
     *
     * @param authRequest the authentication request containing username (email) and password
     * @return ResponseEntity containing AuthResponse with access and refresh tokens
     * @throws jakarta.validation.ConstraintViolationException if request validation fails
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        AuthResponse response = authService.login(authRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Validates the provided JWT token for authenticity and expiration.
     *
     * @param tokenRequest the token request containing the token to validate
     * @return ResponseEntity with boolean indicating token validity with HTTP 200 status
     * @throws ConstraintViolationException if request validation fails
     */
    @PostMapping("/validate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> validate(@Valid @RequestBody TokenRequest tokenRequest) {
        boolean isValid = authService.validate(tokenRequest);
        return ResponseEntity.ok(isValid);
    }

    /**
     * Refreshes the authentication tokens using a valid refresh token.
     * Generates new access and refresh tokens while invalidating the old refresh token.
     *
     * @param tokenRequest the token request containing a valid refresh token
     * @return ResponseEntity containing AuthResponse with new tokens with HTTP 200 status
     * @throws ConstraintViolationException if request validation fails
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody TokenRequest tokenRequest) {
        AuthResponse response = authService.refresh(tokenRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Registers a new user in the system and returns initial authentication tokens.
     *
     * @param registrationRequest the registration request containing user credentials
     * @return ResponseEntity containing AuthResponse with access and refresh tokens with HTTP 201 status
     * @throws AlreadyExistsException       if user already exists
     * @throws ConstraintViolationException if request validation fails
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegistrationRequest registrationRequest) {
        AuthResponse authResponse = authService.register(registrationRequest);
        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }
}
