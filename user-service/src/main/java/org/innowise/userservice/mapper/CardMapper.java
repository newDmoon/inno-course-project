package org.innowise.userservice.mapper;

import org.innowise.userservice.model.dto.CardDTO;
import org.innowise.userservice.model.entity.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CardMapper {
    @Mapping(target = "id", ignore = true)
    Card toEntity(CardDTO cardDTO);

    @Mapping(source = "user.id", target = "userId")
    CardDTO toDto(Card card);

    void updateCardFromDto(CardDTO cardDTO, @MappingTarget Card card);
}
