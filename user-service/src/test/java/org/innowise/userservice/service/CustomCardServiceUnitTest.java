package org.innowise.userservice.service;

import org.innowise.userservice.exception.EmptyResourceException;
import org.innowise.userservice.exception.NotFoundException;
import org.innowise.userservice.mapper.CardMapper;
import org.innowise.userservice.model.dto.CardDTO;
import org.innowise.userservice.model.entity.Card;
import org.innowise.userservice.model.entity.User;
import org.innowise.userservice.repository.CardRepository;
import org.innowise.userservice.repository.UserRepository;
import org.innowise.userservice.service.impl.CustomCardService;
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
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomCardServiceUnitTest {
    @Mock
    private CardRepository cardRepository;
    @Mock
    private CardMapper cardMapper;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private CustomCardService customCardService;
    private CardDTO validDto;

    @BeforeEach
    void setUp() {
        validDto = new CardDTO(
                1L,
                1L,
                "1234567890123",
                "Dmitry Novogrodsky",
                LocalDate.now().plusYears(1)
        );
    }

    @Test
    void createCard_WhenUserExists_ReturnsCardDTO() {
        User user = new User();
        user.setId(1L);
        Card entity = new Card();
        entity.setId(1L);
        entity.setUser(user);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cardMapper.toEntity(validDto)).thenReturn(entity);
        when(cardRepository.save(entity)).thenReturn(entity);
        when(cardMapper.toDto(entity)).thenReturn(validDto);

        CardDTO result = customCardService.createCard(validDto);

        assertEquals(validDto, result);
        verify(userRepository).findById(1L);
        verify(cardRepository).save(entity);
    }

    @Test
    void createCard_WhenUserNotFound_ThrowsNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> customCardService.createCard(validDto));
    }

    @Test
    void getCardById_WhenCardExists_ReturnsCardDTO() {
        Card card = new Card();

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardMapper.toDto(card)).thenReturn(validDto);

        CardDTO result = customCardService.getCardById(1L);

        assertEquals(validDto, result);
    }

    @Test
    void getCardById_WhenCardNotFound_ThrowsNotFoundException() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> customCardService.getCardById(1L));
    }

    @Test
    void updateCardById_WhenCardExists_UpdatesEntity() {
        Card card = new Card();

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        customCardService.updateCardById(validDto);

        verify(cardMapper).updateCardFromDto(validDto, card);
        verify(cardRepository).save(card);
    }

    @Test
    void updateCardById_WhenCardNotFound_ThrowsNotFoundException() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> customCardService.updateCardById(validDto));
    }

    @Test
    void deleteCardById_WhenCardExists_DeletesAndReturnsTrue() {
        when(cardRepository.existsById(1L)).thenReturn(true).thenReturn(false);

        boolean result = customCardService.deleteCardById(1L);

        assertTrue(result);
        assertThrows(NotFoundException.class, () -> customCardService.deleteCardById(1L));
        verify(cardRepository).deleteById(1L);
    }

    @Test
    void deleteCardById_WhenCardNotFound_ThrowsNotFoundException() {
        when(cardRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> customCardService.deleteCardById(1L));
    }

    @Test
    void getCards_WhenIdsProvidedAndCardsExist_ReturnsPageOfDTOs() {
        Card card = new Card();
        Page<Card> page = new PageImpl<>(List.of(card));

        when(cardRepository.findAllByIdIn(eq(List.of(1L)), any(Pageable.class))).thenReturn(page);
        when(cardMapper.toDto(card)).thenReturn(validDto);

        Page<CardDTO> result = customCardService.getCards(List.of(1L), Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
        assertEquals(validDto, result.getContent().getFirst());
    }

    @Test
    void getCards_WhenNoIdsProvidedAndEmptyResult_ThrowsEmptyResourceException() {
        Page<Card> emptyPage = Page.empty();
        Pageable pageable = Pageable.unpaged();

        when(cardRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        assertThrows(EmptyResourceException.class,
                () -> customCardService.getCards(null, pageable));
    }
}
