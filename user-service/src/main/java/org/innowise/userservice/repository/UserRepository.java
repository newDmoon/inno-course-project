package org.innowise.userservice.repository;

import org.innowise.userservice.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    @Override
    Optional<User> findById(Long id);

    @Override
    List<User> findAll();

    Page<User> findAllByIdIn(List<Long> ids, Pageable pageable);

    @Query("Select u FROM User u WHERE u.name = :name AND u.surname = :surname")
    List<User> findByNameAndSurname(String name, String surname);

    @Query("SELECT u FROM User u WHERE u.id IN :ids")
    List<User> findByIds(List<Long> ids);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE users SET name = :name, surname = :surname WHERE id = :id", nativeQuery = true)
    void updateFullNameById(Long id, String name, String surname);
}
