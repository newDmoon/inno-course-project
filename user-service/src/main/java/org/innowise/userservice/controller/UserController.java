package org.innowise.userservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.innowise.userservice.exception.NotFoundException;
import org.innowise.userservice.model.dto.UserDTO;
import org.innowise.userservice.model.dto.UserFilterDTO;
import org.innowise.userservice.service.UserService;
import org.innowise.userservice.util.ApplicationConstant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for managing user operations.
 * Provides endpoints for creating, retrieving, updating, and deleting users.
 *
 * <p>All endpoints are prefixed with {@code /api/v1/users} and support standard HTTP methods
 * with appropriate status codes and validation.</p>
 *
 * @version 1.0
 * @see UserService
 * @see UserDTO
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    /**
     * Retrieves a paginated list of users with optional filtering.
     * Supports pagination through Spring Data's Pageable.
     *
     * @param filter optional filter criteria for searching users (can be {@code null})
     * @param pageable pagination configuration including page number, size
     * @return {@link ResponseEntity} containing a {@link Page} of {@link UserDTO} objects
     *         with HTTP status 200 (OK)
     * @apiNote Example: GET /api/v1/users?page=0&size=10
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Page<UserDTO>> getUsers(
            UserFilterDTO filter,
            @PageableDefault Pageable pageable) {
        Page<UserDTO> usersPage = userService.getUsers(filter, pageable);
        return ResponseEntity.ok(usersPage);
    }

    /**
     * Retrieves a specific user by their unique identifier.
     * Validates that the ID is a positive number.
     *
     * @param id the unique identifier of the user (must be a positive number)
     * @return {@link ResponseEntity} containing the {@link UserDTO} with HTTP status 200 (OK)
     * @throws NotFoundException if no user exists with the given ID
     * @apiNote Example: GET /api/v1/users/123
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<UserDTO> getUserById(@PathVariable(ApplicationConstant.ID) @Positive Long id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Creates a new user in the system.
     * Validates the request body and returns the created user with generated ID.
     *
     * @param userRequest the user data for creation (must be valid
     * @return {@link ResponseEntity} containing the created {@link UserDTO}
     *         with HTTP status 201 (CREATED)
     * @apiNote Example: POST /api/v1/users
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVICE')")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userRequest) {
        UserDTO createdUser = userService.createUser(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    /**
     * Updates an existing user's information.
     * Validates the request body and performs a full update of the user data.
     *
     * @param updateUserRequest the updated user data (must be valid and not {@code null})
     * @return {@link ResponseEntity} containing the updated user data with HTTP status 200 (OK)
     * @throws NotFoundException if no user exists with the given ID
     * @apiNote Example: PUT /api/v1/users
     */
    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<UserDTO> updateUser(@Valid @RequestBody UserDTO updateUserRequest) {
        UserDTO updatedUser = userService.updateUserById(updateUserRequest);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Deletes a user by their unique identifier.
     * Validates that the ID is at least 1.
     *
     * @param id the unique identifier of the user to delete (must be at least 1)
     * @return {@link ResponseEntity} with no content and HTTP status 204 (NO_CONTENT)
     * @apiNote Example: DELETE /api/v1/users/123
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable(ApplicationConstant.ID) @Min(1) Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}
