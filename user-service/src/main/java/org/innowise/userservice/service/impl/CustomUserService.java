package org.innowise.userservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.innowise.userservice.exception.AlreadyExistsException;
import org.innowise.userservice.exception.NotFoundException;
import org.innowise.userservice.mapper.UserMapper;
import org.innowise.userservice.model.dto.UserDTO;
import org.innowise.userservice.model.dto.UserFilterDTO;
import org.innowise.userservice.model.entity.User;
import org.innowise.userservice.repository.UserRepository;
import org.innowise.userservice.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomUserService implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDTO createUser(UserDTO createUserRequest) {
        if (userRepository.existsByEmail(createUserRequest.email())) {
            throw new AlreadyExistsException();
        }

        User newUser = userMapper.toEntity(createUserRequest);
        User savedUser = userRepository.save(newUser);
        return userMapper.toDto(savedUser);
    }

    @Override
    public UserDTO getUserById(Long id) {
        User foundUser = userRepository.findById(id).orElseThrow(NotFoundException::new);
        return userMapper.toDto(foundUser);
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        User foundUser = userRepository.findByEmail(email).orElseThrow(NotFoundException::new);
        return userMapper.toDto(foundUser);
    }

    @Override
    @Transactional
    public void updateUserById(UserDTO updateUserRequest) {
        User existingUser = userRepository.findById(updateUserRequest.id())
                .orElseThrow(NotFoundException::new);

        userMapper.updateUserFromDto(updateUserRequest, existingUser);
        userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public boolean deleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException();
        }
        userRepository.deleteById(id);
        return !userRepository.existsById(id);
    }

    @Override
    public Page<UserDTO> getUsers(UserFilterDTO filter, Pageable pageable) {
        Page<User> usersPage;

        if (filter.ids() != null && !filter.ids().isEmpty()) {
            usersPage = userRepository.findAllByIdIn(filter.ids(), pageable);
        } else if (filter.email() != null && !filter.email().isBlank()) {
            User user = userRepository.findByEmail(filter.email()).orElseThrow(NotFoundException::new);
            usersPage = new PageImpl<>(List.of(user), pageable, 1);
        } else {
            usersPage = userRepository.findAll(pageable);
        }

        return usersPage.map(userMapper::toDto);
    }
}
