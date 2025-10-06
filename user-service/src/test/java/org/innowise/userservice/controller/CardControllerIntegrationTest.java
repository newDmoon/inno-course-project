package org.innowise.userservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.innowise.userservice.model.dto.CardDTO;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
class CardControllerIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "/api/v1/cards";
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "card_info", "users");
    }

    @Test
    void getUsers_WhenNoUsersExist_ReturnsEmptyPage() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "?page=0&size=10", String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void createCard_WhenValidDataProvided_ReturnsCreatedCardDTO() {
        Long userId = createTestUser();

        CardDTO cardRequest = new CardDTO(null, userId, "7777777777777777", "Dmitry Popov", LocalDate.now().plusYears(2));

        ResponseEntity<CardDTO> response = restTemplate.postForEntity(baseUrl, cardRequest, CardDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("7777777777777777", response.getBody().number());
        assertEquals("Dmitry Popov", response.getBody().holder());
    }

    @Test
    void createCard_WhenInvalidDataProvided_ReturnsBadRequest() {
        Long userId = createTestUser();

        CardDTO invalidCard = new CardDTO(null, userId, "123", "holder", LocalDate.now().minusDays(1));

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, invalidCard, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void createCard_WhenInvalidCardNumberFormat_ReturnsBadRequest() {
        Long userId = createTestUser();

        CardDTO invalidCard = new CardDTO(null, userId, "1234abc567890123", "Dmitry Popov", LocalDate.now().plusYears(1));

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, invalidCard, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void createCard_WhenUserNotExists_ReturnsNotFound() {
        CardDTO cardRequest = new CardDTO(null, 99999L, "7777777777777777", "Dmitry Popov", LocalDate.now().plusYears(2));

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, cardRequest, String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getCardById_WhenCardExists_ReturnsCardDTO() {
        Long userId = createTestUser();
        CardDTO createdCard = createTestCard(userId, "7777777777777777", "Dmitry Popov");

        ResponseEntity<CardDTO> response = restTemplate.getForEntity(baseUrl + "/" + createdCard.id(), CardDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(createdCard.id(), response.getBody().id());
        assertEquals("Dmitry Popov", response.getBody().holder());
    }

    @Test
    void getCardById_WhenCardNotExists_ReturnsNotFound() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/99999", String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getCardById_WhenInvalidIdProvided_ReturnsBadRequest() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/0", String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getCards_WithIdsFilter_ReturnsFilteredCards() throws JsonProcessingException {
        Long userId = createTestUser();
        CardDTO card1 = createTestCard(userId, "1111111111111111", "User One");
        CardDTO card2 = createTestCard(userId, "2222222222222222", "User Two");

        String idsParam = card1.id() + "," + card2.id();
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "?ids=" + idsParam + "&page=0&size=10", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        JsonNode root = objectMapper.readTree(response.getBody());
        JsonNode content = root.get("content");

        assertEquals(2, content.size());
    }

    @Test
    void getCards_WithoutFilters_ReturnsAllCards() throws JsonProcessingException {
        Long userId = createTestUser();
        createTestCard(userId, "3333333333333333", "User Three");
        createTestCard(userId, "4444444444444444", "User Four");

        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "?page=0&size=10", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        JsonNode root = objectMapper.readTree(response.getBody());
        JsonNode content = root.get("content");

        assertTrue(content.size() >= 2);
    }

    @Test
    void updateCard_WhenCardExists_ReturnsNoContent() {
        Long userId = createTestUser();
        CardDTO createdCard = createTestCard(userId, "1111111111111111", "Original Holder");

        CardDTO updateRequest = new CardDTO(createdCard.id(), userId, "7777777777777777", "Updated Holder", LocalDate.now().plusYears(3));

        ResponseEntity<Void> response = restTemplate.exchange(baseUrl, HttpMethod.PUT, new HttpEntity<>(updateRequest), Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void updateCard_WhenCardNotExists_ReturnsNotFound() {
        Long userId = createTestUser();

        CardDTO updateRequest = new CardDTO(99999L, userId, "7777777777777777", "Non-existent Card", LocalDate.now().plusYears(1));

        ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.PUT, new HttpEntity<>(updateRequest), String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updateCard_WhenInvalidData_ReturnsBadRequest() {
        Long userId = createTestUser();
        CardDTO createdCard = createTestCard(userId, "7777777777777777", "Test Holder");

        CardDTO invalidUpdateRequest = new CardDTO(createdCard.id(), userId, "123", "X", LocalDate.now().minusDays(1));

        ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.PUT, new HttpEntity<>(invalidUpdateRequest), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void deleteCard_WhenCardExists_ReturnsNoContent() {
        Long userId = createTestUser();
        CardDTO createdCard = createTestCard(userId, "7777777777777777", "To Delete");

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(baseUrl + "/" + createdCard.id(), HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        ResponseEntity<String> getResponse = restTemplate.getForEntity(baseUrl + "/" + createdCard.id(), String.class);
        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }

    @Test
    void deleteCard_WhenCardNotExists_ReturnsNotFound() {
        ResponseEntity<Void> response = restTemplate.exchange(baseUrl + "/99999", HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteCard_WhenInvalidIdProvided_ReturnsBadRequest() {
        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/0", HttpMethod.DELETE, null, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    private Long createTestUser() {
        String userUrl = "/api/v1/users";

        UserDTO userRequest = new UserDTO(null, "testuser12345@example.com", "Test", "User", LocalDate.of(1990, 1, 1), Collections.emptyList());

        ResponseEntity<UserDTO> response = restTemplate.postForEntity(userUrl, userRequest, UserDTO.class);
        if (response.getStatusCode() == HttpStatus.CREATED) {
            return response.getBody().id();
        }
        throw new RuntimeException("Failed to create test user. Status: " + response.getStatusCode());
    }

    private CardDTO createTestCard(Long userId, String cardNumber, String cardHolder) {
        CardDTO cardRequest = new CardDTO(null, userId, cardNumber, cardHolder, LocalDate.now().plusYears(2));

        ResponseEntity<CardDTO> response = restTemplate.postForEntity(baseUrl, cardRequest, CardDTO.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        return response.getBody();
    }
}