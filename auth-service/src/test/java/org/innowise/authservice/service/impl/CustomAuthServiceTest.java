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
import org.innowise.authservice.service.CustomUserDetailsService;
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

    private final String TEST_EMAIL = "test@example.com";
    private final String TEST_PASSWORD = "password123";
    private final String TEST_ACCESS_TOKEN = "access-token";
    private final String TEST_REFRESH_TOKEN = "refresh-token";
    private final String ENCODED_PASSWORD = "encoded-password";

    @Test
    void login_WithValidCredentials_ShouldReturnAuthResponse() {
        AuthRequest authRequest = new AuthRequest(TEST_EMAIL, TEST_PASSWORD);
        User user = new User();
        user.setEmail(TEST_EMAIL);
        user.setPasswordHash(ENCODED_PASSWORD);

        UserDetails userDetails = mock(UserDetails.class);

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(TEST_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);
        when(customUserDetailsService.loadUserByUsername(TEST_EMAIL)).thenReturn(userDetails);
        when(jwtTokenProvider.generateAccessToken(userDetails)).thenReturn(TEST_ACCESS_TOKEN);
        when(jwtTokenProvider.generateRefreshToken(userDetails)).thenReturn(TEST_REFRESH_TOKEN);

        AuthResponse result = customAuthService.login(authRequest);

        assertNotNull(result);
        assertEquals(TEST_ACCESS_TOKEN, result.accessToken());
        assertEquals(TEST_REFRESH_TOKEN, result.refreshToken());

        verify(userRepository).findByEmail(TEST_EMAIL);
        verify(passwordEncoder).matches(TEST_PASSWORD, ENCODED_PASSWORD);
        verify(customUserDetailsService).loadUserByUsername(TEST_EMAIL);
        verify(jwtTokenProvider).generateAccessToken(userDetails);
        verify(jwtTokenProvider).generateRefreshToken(userDetails);
    }

    @Test
    void login_WithNonExistentUser_ShouldThrowNotFoundException() {
        AuthRequest authRequest = new AuthRequest(TEST_EMAIL, TEST_PASSWORD);

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> customAuthService.login(authRequest));

        verify(userRepository).findByEmail(TEST_EMAIL);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(customUserDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtTokenProvider, never()).generateAccessToken(any());
        verify(jwtTokenProvider, never()).generateRefreshToken(any());
    }

    @Test
    void login_WithInvalidPassword_ShouldThrowBadCredentialsException() {
        AuthRequest authRequest = new AuthRequest(TEST_EMAIL, TEST_PASSWORD);
        User user = new User();
        user.setEmail(TEST_EMAIL);
        user.setPasswordHash(ENCODED_PASSWORD);

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(TEST_PASSWORD, ENCODED_PASSWORD)).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> customAuthService.login(authRequest));

        verify(userRepository).findByEmail(TEST_EMAIL);
        verify(passwordEncoder).matches(TEST_PASSWORD, ENCODED_PASSWORD);
        verify(customUserDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtTokenProvider, never()).generateAccessToken(any());
        verify(jwtTokenProvider, never()).generateRefreshToken(any());
    }

    @Test
    void register_WithNewUser_ShouldReturnAuthResponse() {
        AuthRequest authRequest = new AuthRequest(TEST_EMAIL, TEST_PASSWORD);
        Role userRole = new Role();
        userRole.setName(Permission.ROLE_USER);

        UserDetails userDetails = mock(UserDetails.class);

        when(userRepository.existsByEmail(TEST_EMAIL)).thenReturn(false);
        when(roleRepository.findByName(Permission.ROLE_USER)).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(customUserDetailsService.loadUserByUsername(TEST_EMAIL)).thenReturn(userDetails);
        when(jwtTokenProvider.generateAccessToken(userDetails)).thenReturn(TEST_ACCESS_TOKEN);
        when(jwtTokenProvider.generateRefreshToken(userDetails)).thenReturn(TEST_REFRESH_TOKEN);

        AuthResponse result = customAuthService.register(authRequest);

        assertNotNull(result);
        assertEquals(TEST_ACCESS_TOKEN, result.accessToken());
        assertEquals(TEST_REFRESH_TOKEN, result.refreshToken());

        verify(userRepository).existsByEmail(TEST_EMAIL);
        verify(roleRepository).findByName(Permission.ROLE_USER);
        verify(passwordEncoder).encode(TEST_PASSWORD);
        verify(userRepository).save(any(User.class));
        verify(customUserDetailsService).loadUserByUsername(TEST_EMAIL);
        verify(jwtTokenProvider).generateAccessToken(userDetails);
        verify(jwtTokenProvider).generateRefreshToken(userDetails);
    }

    @Test
    void register_WithExistingUser_ShouldThrowAlreadyExistsException() {
        AuthRequest authRequest = new AuthRequest(TEST_EMAIL, TEST_PASSWORD);

        when(userRepository.existsByEmail(TEST_EMAIL)).thenReturn(true);

        assertThrows(AlreadyExistsException.class, () -> customAuthService.register(authRequest));

        verify(userRepository).existsByEmail(TEST_EMAIL);
        verify(roleRepository, never()).findByName(any());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(customUserDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtTokenProvider, never()).generateAccessToken(any());
        verify(jwtTokenProvider, never()).generateRefreshToken(any());
    }

    @Test
    void register_WhenRoleNotFound_ShouldThrowNotFoundException() {
        AuthRequest authRequest = new AuthRequest(TEST_EMAIL, TEST_PASSWORD);

        when(userRepository.existsByEmail(TEST_EMAIL)).thenReturn(false);
        when(roleRepository.findByName(Permission.ROLE_USER)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> customAuthService.register(authRequest));

        verify(userRepository).existsByEmail(TEST_EMAIL);
        verify(roleRepository).findByName(Permission.ROLE_USER);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(customUserDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtTokenProvider, never()).generateAccessToken(any());
        verify(jwtTokenProvider, never()).generateRefreshToken(any());
    }

    @Test
    void refresh_WithValidRefreshToken_ShouldReturnNewAuthResponse() {
        TokenRequest tokenRequest = new TokenRequest(TEST_REFRESH_TOKEN);
        UserDetails userDetails = mock(UserDetails.class);

        when(jwtTokenProvider.validateToken(TEST_REFRESH_TOKEN)).thenReturn(true);
        when(jwtTokenProvider.isRefreshToken(TEST_REFRESH_TOKEN)).thenReturn(true);
        when(jwtTokenProvider.extractUsername(TEST_REFRESH_TOKEN)).thenReturn(TEST_EMAIL);
        when(customUserDetailsService.loadUserByUsername(TEST_EMAIL)).thenReturn(userDetails);
        when(jwtTokenProvider.generateAccessToken(userDetails)).thenReturn("new-access-token");
        when(jwtTokenProvider.generateRefreshToken(userDetails)).thenReturn("new-refresh-token");

        AuthResponse result = customAuthService.refresh(tokenRequest);

        assertNotNull(result);
        assertEquals("new-access-token", result.accessToken());
        assertEquals("new-refresh-token", result.refreshToken());

        verify(jwtTokenProvider).validateToken(TEST_REFRESH_TOKEN);
        verify(jwtTokenProvider).isRefreshToken(TEST_REFRESH_TOKEN);
        verify(jwtTokenProvider).extractUsername(TEST_REFRESH_TOKEN);
        verify(customUserDetailsService).loadUserByUsername(TEST_EMAIL);
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