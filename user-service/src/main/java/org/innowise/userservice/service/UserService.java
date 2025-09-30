package org.innowise.userservice.service;

import org.innowise.userservice.model.dto.UserDTO;
import org.innowise.userservice.model.dto.UserFilterDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for managing user operations and business logic.
 * Provides methods for creating, retrieving, updating, and deleting users.
 */
public interface UserService {
    /**
     * Creates a new user in the system based on the provided user data.
     *
     * @param createUserRequest the user data transfer object containing
     *        all necessary information for user creation
     * @return {@link UserDTO} representing the newly created user with
     *         generated identifier and complete user information
     */
    UserDTO createUser(UserDTO createUserRequest);

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id the unique identifier of the user to retrieve
     * @return {@link UserDTO} containing the complete user information
     */
    UserDTO getUserById(Long id);

    /**
     * Retrieves a user by their email address.
     *
     * @param email the email address of the user to retrieve
     * @return {@link UserDTO} containing the complete user information
     */
    UserDTO getUserByEmail(String email);

    /**
     * Updates an existing user's information by their unique identifier.
     *
     * @param updateUserRequest the user data transfer object containing
     *        the updated information and user identifier
     * @return {@link UserDTO} containing updated user information
     */
    UserDTO updateUserById(UserDTO updateUserRequest);

    /**
     * Deletes a user by their unique identifier.
     *
     * @param id the unique identifier of the user to delete
     * @return {@code true} if the user was successfully deleted,
     *         {@code false} if got some troubles with deletion
     */
    boolean deleteUserById(Long id);

    /**
     * Retrieves a paginated list of users based on the provided filter criteria.
     *
     * @param filter the filter criteria for searching users (can be {@code null} for no filtering)
     * @param pageable the pagination information
     * @return {@link Page} of {@link UserDTO} objects containing the filtered and paginated user results
     */
    Page<UserDTO> getUsers(UserFilterDTO filter, Pageable pageable);
}
