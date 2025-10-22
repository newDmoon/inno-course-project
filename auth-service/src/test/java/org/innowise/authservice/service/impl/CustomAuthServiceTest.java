package org.innowise.authservice.service.impl;

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
import org.innowise.authservice.util.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomAuthServiceTest {
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private CustomUserDetailsService customUserDetailsService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @InjectMocks
    private CustomAuthService customAuthService;

    private final String testEmail = "test@example.com";
    private final String testPassword = "password123";
    private final String testAccessToken = "access-token";
    private final String testRefreshToken = "refresh-token";
    private final String encodedPassword = "encoded-password";

    @Test
    void login_WithValidCredentials_ShouldReturnAuthResponse() {
        AuthRequest authRequest = new AuthRequest(testEmail, testPassword);
        User user = new User();
        user.setEmail(testEmail);
        user.setPasswordHash(encodedPassword);

        UserDetails userDetails = mock(UserDetails.class);

        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(testPassword, encodedPassword)).thenReturn(true);
        when(customUserDetailsService.loadUserByUsername(testEmail)).thenReturn(userDetails);
        when(jwtTokenProvider.generateAccessToken(userDetails)).thenReturn(testAccessToken);
        when(jwtTokenProvider.generateRefreshToken(userDetails)).thenReturn(testRefreshToken);

        AuthResponse result = customAuthService.login(authRequest);

        assertNotNull(result);
        assertEquals(testAccessToken, result.accessToken());
        assertEquals(testRefreshToken, result.refreshToken());

        verify(userRepository).findByEmail(testEmail);
        verify(passwordEncoder).matches(testPassword, encodedPassword);
        verify(customUserDetailsService).loadUserByUsername(testEmail);
        verify(jwtTokenProvider).generateAccessToken(userDetails);
        verify(jwtTokenProvider).generateRefreshToken(userDetails);
    }

    @Test
    void login_WithNonExistentUser_ShouldThrowNotFoundException() {
        AuthRequest authRequest = new AuthRequest(testEmail, testPassword);

        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> customAuthService.login(authRequest));

        verify(userRepository).findByEmail(testEmail);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(customUserDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtTokenProvider, never()).generateAccessToken(any());
        verify(jwtTokenProvider, never()).generateRefreshToken(any());
    }

    @Test
    void login_WithInvalidPassword_ShouldThrowBadCredentialsException() {
        AuthRequest authRequest = new AuthRequest(testEmail, testPassword);
        User user = new User();
        user.setEmail(testEmail);
        user.setPasswordHash(encodedPassword);

        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(testPassword, encodedPassword)).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> customAuthService.login(authRequest));

        verify(userRepository).findByEmail(testEmail);
        verify(passwordEncoder).matches(testPassword, encodedPassword);
        verify(customUserDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtTokenProvider, never()).generateAccessToken(any());
        verify(jwtTokenProvider, never()).generateRefreshToken(any());
    }

    @Test
    void register_WithNewUser_ShouldReturnAuthResponse() {
        AuthRequest authRequest = new AuthRequest(testEmail, testPassword);
        Role userRole = new Role();
        userRole.setName(Permission.ROLE_USER);

        UserDetails userDetails = mock(UserDetails.class);

        when(userRepository.existsByEmail(testEmail)).thenReturn(false);
        when(roleRepository.findByName(Permission.ROLE_USER)).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(testPassword)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(customUserDetailsService.loadUserByUsername(testEmail)).thenReturn(userDetails);
        when(jwtTokenProvider.generateAccessToken(userDetails)).thenReturn(testAccessToken);
        when(jwtTokenProvider.generateRefreshToken(userDetails)).thenReturn(testRefreshToken);

        AuthResponse result = customAuthService.register(authRequest);

        assertNotNull(result);
        assertEquals(testAccessToken, result.accessToken());
        assertEquals(testRefreshToken, result.refreshToken());

        verify(userRepository).existsByEmail(testEmail);
        verify(roleRepository).findByName(Permission.ROLE_USER);
        verify(passwordEncoder).encode(testPassword);
        verify(userRepository).save(any(User.class));
        verify(customUserDetailsService).loadUserByUsername(testEmail);
        verify(jwtTokenProvider).generateAccessToken(userDetails);
        verify(jwtTokenProvider).generateRefreshToken(userDetails);
    }

    @Test
    void register_WithExistingUser_ShouldThrowAlreadyExistsException() {
        AuthRequest authRequest = new AuthRequest(testEmail, testPassword);

        when(userRepository.existsByEmail(testEmail)).thenReturn(true);

        assertThrows(AlreadyExistsException.class, () -> customAuthService.register(authRequest));

        verify(userRepository).existsByEmail(testEmail);
        verify(roleRepository, never()).findByName(any());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(customUserDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtTokenProvider, never()).generateAccessToken(any());
        verify(jwtTokenProvider, never()).generateRefreshToken(any());
    }

    @Test
    void register_WhenRoleNotFound_ShouldThrowNotFoundException() {
        AuthRequest authRequest = new AuthRequest(testEmail, testPassword);

        when(userRepository.existsByEmail(testEmail)).thenReturn(false);
        when(roleRepository.findByName(Permission.ROLE_USER)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> customAuthService.register(authRequest));

        verify(userRepository).existsByEmail(testEmail);
        verify(roleRepository).findByName(Permission.ROLE_USER);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(customUserDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtTokenProvider, never()).generateAccessToken(any());
        verify(jwtTokenProvider, never()).generateRefreshToken(any());
    }

    @Test
    void refresh_WithValidRefreshToken_ShouldReturnNewAuthResponse() {
        TokenRequest tokenRequest = new TokenRequest(testRefreshToken);
        UserDetails userDetails = mock(UserDetails.class);

        when(jwtTokenProvider.validateToken(testRefreshToken)).thenReturn(true);
        when(jwtTokenProvider.isRefreshToken(testRefreshToken)).thenReturn(true);
        when(jwtTokenProvider.extractUsername(testRefreshToken)).thenReturn(testEmail);
        when(customUserDetailsService.loadUserByUsername(testEmail)).thenReturn(userDetails);
        when(jwtTokenProvider.generateAccessToken(userDetails)).thenReturn("new-access-token");
        when(jwtTokenProvider.generateRefreshToken(userDetails)).thenReturn("new-refresh-token");

        AuthResponse result = customAuthService.refresh(tokenRequest);

        assertNotNull(result);
        assertEquals("new-access-token", result.accessToken());
        assertEquals("new-refresh-token", result.refreshToken());

        verify(jwtTokenProvider).validateToken(testRefreshToken);
        verify(jwtTokenProvider).isRefreshToken(testRefreshToken);
        verify(jwtTokenProvider).extractUsername(testRefreshToken);
        verify(customUserDetailsService).loadUserByUsername(testEmail);
        verify(jwtTokenProvider).generateAccessToken(userDetails);
        verify(jwtTokenProvider).generateRefreshToken(userDetails);
    }

    @Test
    void refresh_WithInvalidToken_ShouldThrowAccessDeniedException() {
        TokenRequest tokenRequest = new TokenRequest("invalid-token");

        when(jwtTokenProvider.validateToken("invalid-token")).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> customAuthService.refresh(tokenRequest));

        verify(jwtTokenProvider).validateToken("invalid-token");
        verify(jwtTokenProvider, never()).isRefreshToken(anyString());
        verify(jwtTokenProvider, never()).extractUsername(anyString());
        verify(customUserDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtTokenProvider, never()).generateAccessToken(any());
        verify(jwtTokenProvider, never()).generateRefreshToken(any());
    }


    @Test
    void validate_WithValidToken_ShouldReturnTrue() {
        TokenRequest tokenRequest = new TokenRequest("valid-token");

        when(jwtTokenProvider.validateToken("valid-token")).thenReturn(true);

        boolean result = customAuthService.validate(tokenRequest);

        assertTrue(result);
        verify(jwtTokenProvider).validateToken("valid-token");
    }

    @Test
    void validate_WithInvalidToken_ShouldReturnFalse() {
        TokenRequest tokenRequest = new TokenRequest("invalid-token");

        when(jwtTokenProvider.validateToken("invalid-token")).thenReturn(false);

        boolean result = customAuthService.validate(tokenRequest);

        assertFalse(result);
        verify(jwtTokenProvider).validateToken("invalid-token");
    }
}