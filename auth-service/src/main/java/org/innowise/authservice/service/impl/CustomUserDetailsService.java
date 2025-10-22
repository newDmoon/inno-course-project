package org.innowise.authservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.innowise.authservice.model.entity.Role;
import org.innowise.authservice.model.entity.User;
import org.innowise.authservice.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private static final String ROLE_PART = "ROLE_%s";

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPasswordHash())
                .authorities(getAuthorities(user.getRoles()))
                .build();
    }

    private String[] getAuthorities(Set<Role> roles) {
        return roles.stream()
                .map(role -> ROLE_PART.formatted(role.getName().name()))
                .toArray(String[]::new);
    }
}
