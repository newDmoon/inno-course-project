package org.innowise.authservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.innowise.authservice.exception.AlreadyExistsException;
import org.innowise.authservice.exception.NotFoundException;
import org.innowise.authservice.model.Permission;
import org.innowise.authservice.model.dto.AuthRequest;
import org.innowise.authservice.model.dto.AuthResponse;
import org.innowise.authservice.model.dto.TokenRequest;
import org.innowise.authservice.model.entity.Role;
import org.innowise.authservice.model.entity.User;
import org.innowise.authservice.repository.RoleRepository;
import org.innowise.authservice.repository.UserRepository;
import org.innowise.authservice.service.AuthService;
import org.innowise.authservice.service.CustomUserDetailsService;
import org.innowise.authservice.util.JwtTokenProvider;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomAuthService implements AuthService {
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder encoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new NotFoundException(request.email()));

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
    public AuthResponse register(AuthRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new AlreadyExistsException(request.email());
        }

        Role userRole = roleRepository.findByName(Permission.ROLE_USER)
                .orElseThrow(NotFoundException::new);

        User user = new User();
        user.setEmail(request.email());
        user.setPasswordHash(encoder.encode(request.password()));
        user.setRoles(Set.of(userRole));

        userRepository.save(user);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(request.email());
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
