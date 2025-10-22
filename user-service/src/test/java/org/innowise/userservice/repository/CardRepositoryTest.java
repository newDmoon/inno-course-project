package org.innowise.userservice.repository;

import org.innowise.userservice.model.entity.Card;
import org.innowise.userservice.model.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class CardRepositoryTest {
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private UserRepository userRepository;

    private Card testCard;
    private Card testCard2;
    private User testUser1;
    private User testUser2;

    @BeforeEach
    void setUp() {
        testUser1 = new User();
        testUser1.setEmail("dim@mail.com");
        testUser1.setName("D");
        testUser1.setSurname("N");
        testUser1.setBirthDate(LocalDate.now());
        userRepository.save(testUser1);

        testUser2 = new User();
        testUser2.setEmail("eli@mail.com");
        testUser2.setName("E");
        testUser2.setSurname("M");
        testUser2.setBirthDate(LocalDate.now());
        userRepository.save(testUser2);

        testCard = new Card();
        testCard.setNumber("1111111111111111");
        testCard.setHolder("Dmitry Novogrodsky");
        testCard.setUser(testUser1);

        testCard2 = new Card();
        testCard2.setNumber("2222222222222222");
        testCard2.setHolder("Elizabeth Moch");
        testCard2.setUser(testUser2);

        cardRepository.saveAll(List.of(testCard, testCard2));
    }

    @AfterEach
    void tearDown() {
        cardRepository.deleteAll();
    }

    @Test
    void findByNumber_WhenCardExists_ReturnsCard() {
        Optional<Card> result = cardRepository.findByNumber("1111111111111111");

        assertTrue(result.isPresent());
        assertEquals("Dmitry Novogrodsky", result.get().getHolder());
    }

    @Test
    void findByNumber_WhenCardNotExists_ReturnsEmpty() {
        Optional<Card> result = cardRepository.findByNumber("0000000000000000");

        assertFalse(result.isPresent());
    }

    @Test
    void findAllById_WhenCardsExist_ReturnsCards() {
        List<Long> ids = List.of(testCard.getId(), testCard2.getId());
        List<Card> results = cardRepository.findAllById(ids);

        assertEquals(2, results.size());
        assertTrue(results.contains(testCard));
        assertTrue(results.contains(testCard2));
    }

    @Test
    void findAllById_WhenNoCards_ReturnsEmptyList() {
        List<Card> results = cardRepository.findAllById(List.of(2001L, 1998L));

        assertTrue(results.isEmpty());
    }

    @Test
    void updateHolderByNumber_WhenCardExists_UpdatesHolder() {
        cardRepository.updateHolderByNumber("Updated Holder", "1111111111111111");

        Card updatedCard = cardRepository.findByNumber("1111111111111111").orElseThrow();
        assertEquals("Updated Holder", updatedCard.getHolder());
    }

    @Test
    void updateHolderByNumber_WhenCardNotExists_DoesNotThrowException() {
        assertDoesNotThrow(() -> cardRepository.updateHolderByNumber("New Holder", "0000000000000000"));
    }
}
