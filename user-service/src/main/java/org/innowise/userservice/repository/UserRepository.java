package org.innowise.userservice.repository;

import org.innowise.userservice.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    @Query("Select u FROM User u WHERE u.name = :name AND u.surname = :surname")
    List<User> findByNameAndSurname(@Param("name") String name, @Param("surname") String surname);

    @Query("SELECT u FROM User u WHERE u.id IN :ids")
    List<User> findByIds(@Param("ids") List<Long> ids);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE users SET name = :name, surname = :surname WHERE id = :id", nativeQuery = true)
    void updateFullNameById(@Param("id") Long id, @Param("name") String name, @Param("surname") String surname);
}
