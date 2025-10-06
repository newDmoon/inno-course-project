package org.innowise.userservice.mapper;

import org.innowise.userservice.model.dto.UserDTO;
import org.innowise.userservice.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = CardMapper.class)
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    User toEntity(UserDTO createUserRequest);

    UserDTO toDto(User user);

    @Mapping(target = "cards", ignore = true)
    @Mapping(target = "email", ignore = true)
    void updateUserFromDto(UserDTO updateUserRequest, @MappingTarget User user);
}
