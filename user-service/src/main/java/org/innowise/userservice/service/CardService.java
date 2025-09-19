package org.innowise.userservice.service;

import org.innowise.userservice.model.dto.CardResponse;
import org.innowise.userservice.model.dto.CreateCardRequest;
import org.innowise.userservice.model.dto.UpdateCardRequest;

import java.util.List;

public interface CardService {
    CardResponse createCard(CreateCardRequest createCardRequest);

    CardResponse getCardById(Long id);

    List<CardResponse> getCardsByIds(List<Long> ids);

    void updateCardById(UpdateCardRequest updateCardRequest);

    boolean deleteCardById(Long id);
}
