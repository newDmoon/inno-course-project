package org.innowise.userservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.innowise.userservice.exception.CardNotFoundException;
import org.innowise.userservice.exception.UserNotFoundException;
import org.innowise.userservice.mapper.CardMapper;
import org.innowise.userservice.model.dto.CardResponse;
import org.innowise.userservice.model.dto.CreateCardRequest;
import org.innowise.userservice.model.dto.UpdateCardRequest;
import org.innowise.userservice.model.entity.Card;
import org.innowise.userservice.model.entity.User;
import org.innowise.userservice.repository.CardRepository;
import org.innowise.userservice.repository.UserRepository;
import org.innowise.userservice.service.CardService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomCardService implements CardService {
    private final CardRepository cardRepository;
    private final CardMapper cardMapper;
    private final UserRepository userRepository;

    @Override
    public CardResponse createCard(CreateCardRequest createCardRequest) {
        User user = userRepository.findById(createCardRequest.userId()).orElseThrow(UserNotFoundException::new);
        Card newCard = cardMapper.toEntity(createCardRequest);
        newCard.setUser(user);
        Card savedCard = cardRepository.save(newCard);
        return cardMapper.toDto(savedCard);
    }

    @Override
    public CardResponse getCardById(Long id) {
        Card foundCard = cardRepository.findById(id).orElseThrow(CardNotFoundException::new);
        return cardMapper.toDto(foundCard);
    }

    @Override
    public List<CardResponse> getCardsByIds(List<Long> ids) {
        List<Card> cards = cardRepository.findAllById(ids);
        if (cards.isEmpty()) {
            throw new CardNotFoundException();
        }
        return cardMapper.toDtoList(cards);
    }

    @Override
    public void updateCardById(UpdateCardRequest updateCardRequest) {
        Card exiting = cardRepository.findById(updateCardRequest.id()).orElseThrow(CardNotFoundException::new);
        cardMapper.updateCardFromDto(updateCardRequest, exiting);
        cardRepository.save(exiting);
    }

    @Override
    public boolean deleteCardById(Long id) {
        if (!cardRepository.existsById(id)) {
            throw new CardNotFoundException(id);
        }
        cardRepository.deleteById(id);
        return !cardRepository.existsById(id);
    }
}
