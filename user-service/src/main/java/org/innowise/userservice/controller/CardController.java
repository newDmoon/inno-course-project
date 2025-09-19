package org.innowise.userservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.innowise.userservice.model.dto.CardResponse;
import org.innowise.userservice.model.dto.CreateCardRequest;
import org.innowise.userservice.model.dto.UpdateCardRequest;
import org.innowise.userservice.service.CardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/api/user-service/cards")
@RequiredArgsConstructor
public class CardController {
    private final CardService cardService;

    @PostMapping("/create")
    public ResponseEntity<CardResponse> createCard(@Valid @RequestBody CreateCardRequest cardRequest) {
        CardResponse createdCard = cardService.createCard(cardRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCard);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardResponse> getCardById(@PathVariable Long id) {
        CardResponse card = cardService.getCardById(id);
        return ResponseEntity.ok(card);
    }

    @GetMapping("/some-cards")
    public ResponseEntity<List<CardResponse>> getCardsByIds(@RequestParam List<Long> ids) {
        List<CardResponse> cards = cardService.getCardsByIds(ids);
        return ResponseEntity.ok(cards);
    }

    @PutMapping("/update")
    public ResponseEntity<CardResponse> updateCard(@Valid @RequestBody UpdateCardRequest updateCardRequest) {
        cardService.updateCardById(updateCardRequest);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        cardService.deleteCardById(id);
        return ResponseEntity.noContent().build();
    }
}
