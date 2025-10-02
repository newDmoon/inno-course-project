package org.innowise.userservice.repository;

import org.innowise.userservice.model.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private User testUser2;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("test1@example.com");
        testUser.setName("Dmitry");
        testUser.setSurname("Novogrodsky");

        testUser2 = new User();
        testUser2.setEmail("test2@example.com");
        testUser2.setName("Elizabeth");
        testUser2.setSurname("Moch");

        userRepository.saveAll(List.of(testUser, testUser2));
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void findById_WhenUserExists_ReturnsUser() {
        User savedUser = userRepository.findById(testUser.getId()).orElse(null);

        assertNotNull(savedUser);
        assertEquals(testUser.getName(), savedUser.getName());
        assertEquals(testUser.getEmail(), savedUser.getEmail());
    }

    @Test
    void findByEmail_WhenUserExists_ReturnsUser() {
        Optional<User> result = userRepository.findByEmail("test1@example.com");

        assertTrue(result.isPresent());
        assertEquals("Dmitry", result.get().getName());
        assertEquals("Novogrodsky", result.get().getSurname());
    }

    @Test
    void findByEmail_WhenUserNotExists_ReturnsEmpty() {
        Optional<User> result = userRepository.findByEmail("nonexistent@example.com");

        assertFalse(result.isPresent());
    }

    @Test
    void findByNameAndSurname_WhenUsersExist_ReturnsUsers() {
        List<User> results = userRepository.findByNameAndSurname("Dmitry", "Novogrodsky");

        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals("test1@example.com", results.getFirst().getEmail());
    }

    @Test
    void findByNameAndSurname_WhenNoUsers_ReturnsEmptyList() {
        List<User> results = userRepository.findByNameAndSurname("badName", "badSurname");

        assertTrue(results.isEmpty());
    }

    @Test
    void findByIds_WhenUsersExist_ReturnsUsers() {
        List<Long> ids = List.of(testUser.getId(), testUser2.getId());
        List<User> results = userRepository.findByIds(ids);

        assertEquals(2, results.size());
        assertThat(results).asList().contains(testUser, testUser2);
    }

    @Test
    void findByIds_WhenNoUsers_ReturnsEmptyList() {
        List<User> results = userRepository.findByIds(List.of(2001L, 1998L));

        assertTrue(results.isEmpty());
    }

    @Test
    void updateFullNameById_WhenUserExists_UpdatesUser() {
        userRepository.updateFullNameById(testUser.getId(), "UpdatedName", "UpdatedSurname");

        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertEquals("UpdatedName", updatedUser.getName());
        assertEquals("UpdatedSurname", updatedUser.getSurname());
    }

    @Test
    void updateFullNameById_WhenUserNotExists_DoesNotThrowException() {
        assertDoesNotThrow(() -> userRepository.updateFullNameById(999L, "NewName", "NewSurname"));
    }
}
