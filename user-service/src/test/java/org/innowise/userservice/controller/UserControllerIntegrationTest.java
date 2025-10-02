package org.innowise.userservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.innowise.userservice.model.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
class UserControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "/api/v1/users";
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "users", "card_info");
    }

    @Test
    void createUser_WhenValidDataProvided_ReturnsCreatedUserDTO() {
        UserDTO userRequest = createUserDTO("testuser", "testuser@example.com");

        ResponseEntity<UserDTO> response = restTemplate.postForEntity(baseUrl, userRequest, UserDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("testuser", response.getBody().name());
    }

    @Test
    void createUser_WhenInvalidDataProvided_ReturnsBadRequest() {
        UserDTO invalidUser = createUserDTO("1", "1");

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, invalidUser, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void createUser_WhenInvalidEmailFormat_ReturnsBadRequest() {
        UserDTO invalidUser = UserDTO.builder()
                .email("invalid-email")
                .name("test")
                .surname("user")
                .birthDate(LocalDate.now())
                .cards(Collections.emptyList())
                .build();

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, invalidUser, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getUserById_WhenUserExists_ReturnsUserDTO() {
        UserDTO userRequest = createUserDTO("getuser", "getuser@example.com");
        UserDTO createdUser = restTemplate.postForEntity(baseUrl, userRequest, UserDTO.class).getBody();
        assertNotNull(createdUser);

        ResponseEntity<UserDTO> response = restTemplate.getForEntity(baseUrl + "/" + createdUser.id(), UserDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(createdUser.id(), response.getBody().id());
        assertEquals("getuser", response.getBody().name());
    }

    @Test
    void getUserById_WhenUserNotExists_ReturnsNotFound() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/99999", String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getUserById_WhenInvalidIdProvided_ReturnsBadRequest() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/0", String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getUsers_WithEmailFilter_ReturnsFilteredUsers() throws JsonProcessingException {
        createTestUser("alice", "alice@company.com");
        createTestUser("bob", "bob@company.com");

        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "?email=alice@company.com&page=0&size=10", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.getBody());
        JsonNode content = root.get("content");

        assertEquals(1, content.size());
        assertEquals("alice", content.get(0).get("name").asText());
    }

    @Test
    void updateUser_WhenUserExists_ReturnsUpdatedUserDTO() {
        UserDTO userRequest = createUserDTO("original", "original@test.com");
        UserDTO createdUser = restTemplate.postForEntity(baseUrl, userRequest, UserDTO.class).getBody();
        assertNotNull(createdUser);

        UserDTO updateRequest = UserDTO.builder()
                .id(createdUser.id())
                .email("updated@test.com")
                .name("updated")
                .surname("UpdatedSurname")
                .birthDate(LocalDate.of(1990, 1, 1))
                .cards(Collections.emptyList())
                .build();

        ResponseEntity<UserDTO> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest),
                UserDTO.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(createdUser.id(), response.getBody().id());
        assertEquals("updated", response.getBody().name());
    }

    @Test
    void updateUser_WhenUserNotExists_ReturnsNotFound() {
        UserDTO updateRequest = UserDTO.builder()
                .id(99999L)
                .email("nonexistent@test.com")
                .name("nonexistent")
                .surname("User")
                .birthDate(LocalDate.now())
                .cards(Collections.emptyList())
                .build();

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest),
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updateUser_WhenInvalidData_ReturnsBadRequest() {
        UserDTO userRequest = createUserDTO("testuser", "test@example.com");
        UserDTO createdUser = restTemplate.postForEntity(baseUrl, userRequest, UserDTO.class).getBody();
        assertNotNull(createdUser);

        UserDTO invalidUpdateRequest = UserDTO.builder()
                .id(createdUser.id())
                .email("invalid-email")
                .name("test")
                .surname("user")
                .birthDate(LocalDate.now())
                .cards(Collections.emptyList())
                .build();

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.PUT,
                new HttpEntity<>(invalidUpdateRequest),
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void deleteUser_WhenUserExists_ReturnsNoContent() {
        UserDTO userRequest = createUserDTO("todelete", "delete@test.com");
        UserDTO createdUser = restTemplate.postForEntity(baseUrl, userRequest, UserDTO.class).getBody();
        assertNotNull(createdUser);

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                baseUrl + "/" + createdUser.id(),
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        ResponseEntity<String> getResponse = restTemplate.getForEntity(
                baseUrl + "/" + createdUser.id(),
                String.class
        );
        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }

    @Test
    void deleteUser_WhenUserNotExists_ReturnsNotFound() {
        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/99999",
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteUser_WhenInvalidIdProvided_ReturnsBadRequest() {
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/0",
                HttpMethod.DELETE,
                null,
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    private UserDTO createUserDTO(String name, String email) {
        return UserDTO.builder()
                .email(email)
                .name(name)
                .surname("TestSurname")
                .birthDate(LocalDate.of(2001, 9, 25))
                .cards(Collections.emptyList())
                .build();
    }

    private void createTestUser(String username, String email) {
        UserDTO userRequest = createUserDTO(username, email);
        restTemplate.postForEntity(baseUrl, userRequest, UserDTO.class);
    }
}