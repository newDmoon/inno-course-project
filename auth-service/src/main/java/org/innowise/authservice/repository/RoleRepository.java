package org.innowise.authservice.repository;

import org.innowise.authservice.model.Permission;
import org.innowise.authservice.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(Permission name);
}
