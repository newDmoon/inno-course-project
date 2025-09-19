package org.innowise.userservice.mapper;

import org.innowise.userservice.model.dto.CreateUserRequest;
import org.innowise.userservice.model.dto.UpdateUserRequest;
import org.innowise.userservice.model.dto.UserResponse;
import org.innowise.userservice.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(CreateUserRequest createUserRequest);

    UserResponse toDto(User user);

    void updateUserFromDto(UpdateUserRequest updateUserRequest, @MappingTarget User user);

    List<UserResponse> toDtoList(List<User> users);
}
