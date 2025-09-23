package org.innowise.userservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.innowise.userservice.exception.UserAlreadyExistsException;
import org.innowise.userservice.exception.UserNotFoundException;
import org.innowise.userservice.mapper.UserMapper;
import org.innowise.userservice.model.dto.CreateUserRequest;
import org.innowise.userservice.model.dto.UpdateUserRequest;
import org.innowise.userservice.model.dto.UserResponse;
import org.innowise.userservice.model.entity.User;
import org.innowise.userservice.repository.UserRepository;
import org.innowise.userservice.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserService implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponse createUser(CreateUserRequest createUserRequest) {
        if (userRepository.existsByEmail(createUserRequest.email())) {
            throw new UserAlreadyExistsException();
        }

        User newUser = userMapper.toEntity(createUserRequest);
        User savedUser = userRepository.save(newUser);
        return userMapper.toDto(savedUser);
    }

    @Override
    public UserResponse getUserById(Long id) {
        User foundUser = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        return userMapper.toDto(foundUser);
    }

    @Override
    public List<UserResponse> getUsersByIds(List<Long> ids) {
        List<User> users = userRepository.findByIds(ids);
        if (users.isEmpty()) {
            throw new UserNotFoundException();
        }
        return userMapper.toDtoList(users);
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        User foundUser = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        return userMapper.toDto(foundUser);
    }

    @Override
    @Transactional
    public void updateUserById(UpdateUserRequest updateUserRequest) {
        User existingUser = userRepository.findById(updateUserRequest.id())
                .orElseThrow(UserNotFoundException::new);

        userMapper.updateUserFromDto(updateUserRequest, existingUser);
        userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public boolean deleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException();
        }
        userRepository.deleteById(id);
        return !userRepository.existsById(id);
    }
}
