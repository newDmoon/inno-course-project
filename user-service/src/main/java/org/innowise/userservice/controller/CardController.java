package org.innowise.userservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.innowise.userservice.exception.NotFoundException;
import org.innowise.userservice.model.dto.CardDTO;
import org.innowise.userservice.service.CardService;
import org.innowise.userservice.util.ApplicationConstant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller for managing card operations.
 * Provides endpoints for creating, retrieving, updating, and deleting cards.
 *
 * <p>All endpoints are prefixed with {@code /api/v1/cards} and support standard HTTP methods
 * with appropriate status codes and validation.</p>
 *
 * @version 1.0
 * @see CardService
 * @see CardDTO
 */
@RestController
@RequestMapping("/api/v1/cards")
@RequiredArgsConstructor
@Validated
public class CardController {
    private final CardService cardService;

    /**
     * Retrieves a paginated list of cards with optional filtering by identifiers.
     * Supports pagination through Spring Data's Pageable.
     *
     * @param ids optional list of card identifiers to filter by (can be {@code null} for all cards)
     * @param pageable pagination configuration including page number, size
     * @return {@link ResponseEntity} containing a {@link Page} of {@link CardDTO} objects
     *         with HTTP status 200 (OK)
     * @apiNote Examples:
     *          GET /api/v1/cards?ids=1,2,3&page=0&size=5
     *          GET /api/v1/cards?page=0&size=10
     */
    @GetMapping
    public ResponseEntity<Page<CardDTO>> getCards(
            @RequestParam(value = ApplicationConstant.IDS, required = false) List<Long> ids,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<CardDTO> cards = cardService.getCards(ids, pageable);
        return ResponseEntity.ok(cards);
    }

    /**
     * Retrieves a specific card by its unique identifier.
     * Validates that the ID is a positive number.
     *
     * @param id the unique identifier of the card (must be a positive number)
     * @return {@link ResponseEntity} containing the {@link CardDTO} with HTTP status 200 (OK)
     * @throws NotFoundException if no card exists with the given ID
     * @apiNote Example: GET /api/v1/cards/123
     */
    @GetMapping("/{id}")
    public ResponseEntity<CardDTO> getCardById(@PathVariable(ApplicationConstant.ID) @Positive Long id) {
        CardDTO card = cardService.getCardById(id);
        return ResponseEntity.ok(card);
    }

    /**
     * Creates a new card in the system.
     * Validates the request body and returns the created card with generated ID.
     *
     * @param cardRequest the card data for creation (must be valid)
     * @throws NotFoundException if no user exists with the given userId
     * @return {@link ResponseEntity} containing the created {@link CardDTO}
     *         with HTTP status 201 (CREATED)
     * @apiNote Example: POST /api/v1/cards
     */
    @PostMapping
    public ResponseEntity<CardDTO> createCard(@Valid @RequestBody CardDTO cardRequest) {
        CardDTO createdCard = cardService.createCard(cardRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCard);
    }

    /**
     * Updates an existing card's information.
     * Validates the request body and performs a full update of the card data.
     *
     * @param updateCardRequest the updated card data (must be valid)
     * @return {@link ResponseEntity} with no content and HTTP status 204 (NO_CONTENT)
     * @throws NotFoundException if no card exists with the given ID
     * @apiNote Example: PUT /api/v1/cards
     */
    @PutMapping
    public ResponseEntity<CardDTO> updateCard(@Valid @RequestBody CardDTO updateCardRequest) {
        cardService.updateCardById(updateCardRequest);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deletes a card by its unique identifier.
     * Validates that the ID is a positive number.
     *
     * @param id the unique identifier of the card to delete (must be a positive number)
     * @return {@link ResponseEntity} with no content and HTTP status 204 (NO_CONTENT)
     * @apiNote Example: DELETE /api/v1/cards/123
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable(ApplicationConstant.ID) @Positive Long id) {
        cardService.deleteCardById(id);
        return ResponseEntity.noContent().build();
    }
}
