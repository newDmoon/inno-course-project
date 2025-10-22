package org.innowise.userservice.service;

import org.innowise.userservice.model.dto.CardDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for managing card operations and business logic.
 * Provides methods for creating, retrieving, updating, and deleting cards.
 */
public interface CardService {
    /**
     * Creates a new card in the system based on the provided card data with validation.
     *
     * @param cardDTO the card data transfer object containing
     *        all necessary information for card creation
     * @return {@link CardDTO} representing the newly created card with
     *         generated identifier and complete card information
     */
    CardDTO createCard(CardDTO cardDTO);

    /**
     * Retrieves a card by its unique identifier.
     *
     * @param id the unique identifier of the card to retrieve
     * @return {@link CardDTO} containing the complete card information
     */
    CardDTO getCardById(Long id);

    /**
     * Updates an existing card's information by its unique identifier.
     *
     * @param cardDTO the card data transfer object containing
     *        the updated information and card identifier
     */
    void updateCardById(CardDTO cardDTO);

    /**
     * Deletes a card by its unique identifier.
     *
     * @param id the unique identifier of the card to delete
     * @return {@code true} if the card was successfully deleted,
     *         {@code false} if got some troubles with deletion
     */
    boolean deleteCardById(Long id);

    /**
     * Retrieves a paginated list of cards based on the provided list of identifiers.
     *
     * @param ids the list of card identifiers to retrieve
     * @param pageable the pagination information
     * @return {@link Page} of {@link CardDTO} objects containing the filtered and paginated card results
     */
    Page<CardDTO> getCards(List<Long> ids, Pageable pageable);
}
