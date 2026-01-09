package org.innowise.authservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.innowise.authservice.client.UserClient;
import org.innowise.authservice.exception.AlreadyExistsException;
import org.innowise.authservice.exception.NotFoundException;
import org.innowise.authservice.model.Permission;
import org.innowise.authservice.model.dto.AuthRequest;
import org.innowise.authservice.model.dto.AuthResponse;
import org.innowise.authservice.model.dto.RegistrationRequest;
import org.innowise.authservice.model.dto.TokenRequest;
import org.innowise.authservice.model.dto.UserDTO;
import org.innowise.authservice.model.entity.Role;
import org.innowise.authservice.model.entity.User;
import org.innowise.authservice.repository.RoleRepository;
import org.innowise.authservice.repository.UserRepository;
import org.innowise.authservice.service.AuthService;
import org.innowise.authservice.util.JwtTokenProvider;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomAuthService implements AuthService {
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder encoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserClient userClient;

    @Override
    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(NotFoundException::new);

        if (!encoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(request.email());

        return new AuthResponse(
                jwtTokenProvider.generateAccessToken(userDetails),
                jwtTokenProvider.generateRefreshToken(userDetails)
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuthResponse register(RegistrationRequest registrationRequest) {
        if (userRepository.existsByEmail(registrationRequest.email())) {
            throw new AlreadyExistsException(registrationRequest.email());
        }

        UserDTO createdUser = null;
        try {
            createdUser = userClient.createUser(new UserDTO(null, registrationRequest.email(), registrationRequest.name(), registrationRequest.surname(), registrationRequest.birthDate()));

            Role userRole = roleRepository.findByName(Permission.ROLE_USER)
                    .orElseThrow(NotFoundException::new);

            User user = new User();
            user.setId(createdUser.id());
            user.setEmail(registrationRequest.email());
            user.setPasswordHash(encoder.encode(registrationRequest.password()));
            user.setRoles(Set.of(userRole));

            userRepository.save(user);

        } catch (Exception e) {
            if (createdUser != null && createdUser.id() != null) {
                try {
                    userClient.deleteUserById(createdUser.id());
                } catch (Exception rollbackEx) {
                    log.error("Rollback failed: could not delete user in user-service", rollbackEx);
                }
            }
            throw new RuntimeException("Failed to register user", e);
        }

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(registrationRequest.email());
        return new AuthResponse(
                jwtTokenProvider.generateAccessToken(userDetails),
                jwtTokenProvider.generateRefreshToken(userDetails)
        );
    }

    @Override
    public AuthResponse refresh(TokenRequest request) {
        String token = request.token();

        if (!jwtTokenProvider.validateToken(token) || !jwtTokenProvider.isRefreshToken(token)) {
            throw new AccessDeniedException("Invalid or expired refresh token");
        }

        String username = jwtTokenProvider.extractUsername(token);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        return new AuthResponse(
                jwtTokenProvider.generateAccessToken(userDetails),
                jwtTokenProvider.generateRefreshToken(userDetails)
        );
    }

    @Override
    public boolean validate(TokenRequest tokenRequest) {
        return jwtTokenProvider.validateToken(tokenRequest.token());
    }
}
