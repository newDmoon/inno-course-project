package org.innowise.userservice.mapper;

import org.innowise.userservice.model.dto.CardResponse;
import org.innowise.userservice.model.dto.CreateCardRequest;
import org.innowise.userservice.model.dto.UpdateCardRequest;
import org.innowise.userservice.model.entity.Card;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CardMapper {
    Card toEntity(CreateCardRequest createCardRequest);

    CardResponse toDto(Card card);

    List<CardResponse> toDtoList(List<Card> cards);

    Card updateCardFromDto(UpdateCardRequest updateCardRequest, @MappingTarget Card card);
}
