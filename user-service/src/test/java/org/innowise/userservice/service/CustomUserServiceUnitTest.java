package org.innowise.userservice.service;

import org.innowise.userservice.exception.AlreadyExistsException;
import org.innowise.userservice.exception.NotFoundException;
import org.innowise.userservice.mapper.UserMapper;
import org.innowise.userservice.model.dto.UserDTO;
import org.innowise.userservice.model.dto.UserFilterDTO;
import org.innowise.userservice.model.entity.User;
import org.innowise.userservice.repository.UserRepository;
import org.innowise.userservice.service.impl.CustomUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserServiceUnitTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private CustomUserService customUserService;
    private UserDTO validUserDTO;

    @BeforeEach
    void setUp() {
        validUserDTO = new UserDTO(1L, "test@example.com", "John", "Doe", LocalDate.now(), List.of());
    }

    @Test
    void createUser_WhenUserDoesNotExist_ReturnsUserDTO() {
        User entity = new User();

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userMapper.toEntity(validUserDTO)).thenReturn(entity);
        when(userRepository.save(entity)).thenReturn(entity);
        when(userMapper.toDto(entity)).thenReturn(validUserDTO);

        UserDTO result = customUserService.createUser(validUserDTO);

        assertEquals(validUserDTO, result);
        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository).save(entity);
    }

    @Test
    void createUser_WhenUserAlreadyExists_ThrowsAlreadyExistsException() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThrows(AlreadyExistsException.class, () -> customUserService.createUser(validUserDTO));
    }

    @Test
    void getUserById_WhenUserExists_ReturnsUserDTO() {
        User newUser = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(newUser));
        when(userMapper.toDto(newUser)).thenReturn(validUserDTO);

        UserDTO result = customUserService.getUserById(1L);

        assertEquals(validUserDTO, result);
    }

    @Test
    void getUserById_WhenUserNotFound_ThrowsNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> customUserService.getUserById(1L));
    }

    @Test
    void getUserByEmail_WhenUserExists_ReturnsUserDTO() {
        User newUser = new User();
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(newUser));
        when(userMapper.toDto(newUser)).thenReturn(validUserDTO);

        UserDTO result = customUserService.getUserByEmail("test@example.com");

        assertEquals(validUserDTO, result);
    }

    @Test
    void getUserByEmail_WhenUserNotFound_ThrowsNotFoundException() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> customUserService.getUserByEmail("test@example.com"));
    }

    @Test
    void updateUserById_WhenUserExists_UpdatesEntity() {
        User newUser = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(newUser));

        customUserService.updateUserById(validUserDTO);

        verify(userMapper).updateUserFromDto(validUserDTO, newUser);
        verify(userRepository).save(newUser);
    }

    @Test
    void updateUserById_WhenUserNotFound_ThrowsNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> customUserService.updateUserById(validUserDTO));
    }

    @Test
    void deleteUserById_WhenUserExists_DeletesAndReturnsTrue() {
        when(userRepository.existsById(1L)).thenReturn(true).thenReturn(false);

        boolean result = customUserService.deleteUserById(1L);

        assertTrue(result);
        assertThrows(NotFoundException.class, () -> customUserService.deleteUserById(1L));
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUserById_WhenUserNotFound_ThrowsNotFoundException() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> customUserService.deleteUserById(1L));
    }

    @Test
    void getUsers_WithEmailFilter_ReturnsPageOfUserDTOs() {
        User newUser = new User();
        UserFilterDTO filter = new UserFilterDTO(null, "test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(newUser));
        when(userMapper.toDto(newUser)).thenReturn(validUserDTO);

        Page<UserDTO> result = customUserService.getUsers(filter, Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
        assertEquals(validUserDTO, result.getContent().getFirst());
    }

    @Test
    void getUsers_WithEmailFilter_WhenUserNotFound_ThrowsNotFoundException() {
        UserFilterDTO filter = new UserFilterDTO(null, "test@example.com");
        Pageable unpaged = Pageable.unpaged();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> customUserService.getUsers(filter, unpaged));
    }

    @Test
    void getUsers_WithNoFilter_ReturnsPageOfUserDTOs() {
        User newUser = new User();
        Page<User> page = new PageImpl<>(List.of(newUser));
        UserFilterDTO filter = new UserFilterDTO(null, null);

        when(userRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(userMapper.toDto(newUser)).thenReturn(validUserDTO);

        Page<UserDTO> result = customUserService.getUsers(filter, Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
        assertEquals(validUserDTO, result.getContent().getFirst());
    }
}
