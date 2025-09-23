package org.innowise.userservice.mapper;

import org.innowise.userservice.model.dto.CardResponse;
import org.innowise.userservice.model.dto.CreateCardRequest;
import org.innowise.userservice.model.dto.UpdateCardRequest;
import org.innowise.userservice.model.entity.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CardMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "userId", target = "user.id")
    Card toEntity(CreateCardRequest createCardRequest);

    @Mapping(source = "user.id", target = "userId")
    CardResponse toDto(Card card);

    List<CardResponse> toDtoList(List<Card> cards);

    void updateCardFromDto(UpdateCardRequest updateCardRequest, @MappingTarget Card card);
}
