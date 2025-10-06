package org.innowise.userservice.repository;

import org.innowise.userservice.model.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    boolean existsById(@NonNull Long id);

    @Override
    Optional<Card> findById(Long id);

    @Override
    void deleteById(Long id);

    Optional<Card> findByNumber(String number);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE card_info SET holder = :holder WHERE number = :number", nativeQuery = true)
    void updateHolderByNumber(String holder, String number);

    @Query("SELECT c FROM Card c WHERE c.id IN :ids")
    List<Card> findAllById(List<Long> ids);

    Page<Card> findAllByIdIn(List<Long> ids, Pageable pageable);
}
