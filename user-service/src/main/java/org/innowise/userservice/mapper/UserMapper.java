package org.innowise.userservice.mapper;

import org.innowise.userservice.model.dto.CreateUserRequest;
import org.innowise.userservice.model.dto.UpdateUserRequest;
import org.innowise.userservice.model.dto.UserResponse;
import org.innowise.userservice.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cards", ignore = true)
    User toEntity(CreateUserRequest createUserRequest);

    UserResponse toDto(User user);

    @Mapping(target = "cards", ignore = true)
    @Mapping(target = "email", ignore = true)
    void updateUserFromDto(UpdateUserRequest updateUserRequest, @MappingTarget User user);

    List<UserResponse> toDtoList(List<User> users);
}
