package org.innowise.userservice.service;

import org.innowise.userservice.model.dto.CreateUserRequest;
import org.innowise.userservice.model.dto.UpdateUserRequest;
import org.innowise.userservice.model.dto.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse createUser(CreateUserRequest createUserRequest);
    UserResponse getUserById(Long id);
    List<UserResponse> getUsersByIds(List<Long> ids);
    UserResponse getUserByEmail(String email);
    void updateUserById(UpdateUserRequest updateUserRequest);
    boolean deleteUserById(Long id);
}
