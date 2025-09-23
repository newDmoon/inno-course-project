package org.innowise.userservice.repository;

import org.innowise.userservice.model.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    boolean existsById(Long id);
    Optional<Card> findByNumber(String number);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE card_info SET holder = :holder WHERE number = :number", nativeQuery = true)
    void updateHolderByNumber(@Param("holder") String holder, @Param("number") String number);

    @Query("SELECT c FROM Card c WHERE c.id IN :ids")
    List<Card> findAllById(@Param("ids") List<Long> ids);
}
