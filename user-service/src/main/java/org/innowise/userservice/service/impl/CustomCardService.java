package org.innowise.userservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.innowise.userservice.exception.EmptyResourceException;
import org.innowise.userservice.exception.NotFoundException;
import org.innowise.userservice.mapper.CardMapper;
import org.innowise.userservice.model.dto.CardDTO;
import org.innowise.userservice.model.entity.Card;
import org.innowise.userservice.model.entity.User;
import org.innowise.userservice.repository.CardRepository;
import org.innowise.userservice.repository.UserRepository;
import org.innowise.userservice.service.CardService;
import org.innowise.userservice.util.ApplicationConstant;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomCardService implements CardService {
    private final CardRepository cardRepository;
    private final CardMapper cardMapper;
    private final UserRepository userRepository;

    @Override
    @CachePut(value = ApplicationConstant.CARDS, key = "#result.id()")
    public CardDTO createCard(CardDTO cardDTO) {
        User user = userRepository.findById(cardDTO.userId()).orElseThrow(NotFoundException::new);
        Card newCard = cardMapper.toEntity(cardDTO);
        newCard.setUser(user);
        Card savedCard = cardRepository.save(newCard);
        return cardMapper.toDto(savedCard);
    }

    @Override
    @Cacheable(value = ApplicationConstant.CARDS, key = "#id")
    public CardDTO getCardById(Long id) {
        Card foundCard = cardRepository.findById(id).orElseThrow(NotFoundException::new);
        return cardMapper.toDto(foundCard);
    }

    @Override
    @Cacheable(value = ApplicationConstant.CARDS, key = "#updateCardRequest.id()")
    public void updateCardById(CardDTO updateCardRequest) {
        Card exiting = cardRepository.findById(updateCardRequest.id()).orElseThrow(NotFoundException::new);
        cardMapper.updateCardFromDto(updateCardRequest, exiting);
        cardRepository.save(exiting);
    }

    @Override
    @CacheEvict(value = ApplicationConstant.CARDS, key = "#id")
    public boolean deleteCardById(Long id) {
        if (!cardRepository.existsById(id)) {
            throw new NotFoundException(id.toString());
        }
        cardRepository.deleteById(id);
        return !cardRepository.existsById(id);
    }

    @Override
    public Page<CardDTO> getCards(List<Long> ids, Pageable pageable) {
        Page<Card> cardsPage;

        if (ids != null && !ids.isEmpty()) {
            cardsPage = cardRepository.findAllByIdIn(ids, pageable);
        } else {
            cardsPage = cardRepository.findAll(pageable);
        }

        if (cardsPage.getTotalElements() == 0){
            throw new EmptyResourceException();
        }
        return cardsPage.map(cardMapper::toDto);
    }
}
