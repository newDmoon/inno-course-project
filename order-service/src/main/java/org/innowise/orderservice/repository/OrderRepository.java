package org.innowise.orderservice.repository;

import org.innowise.orderservice.model.OrderStatus;
import org.innowise.orderservice.model.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findById(Long id);

    @Override
    boolean existsById(Long id);

    Page<Order> findAllByIdIn(List<Long> ids, Pageable pageable);

    Page<Order> findAllByStatusIn(List<OrderStatus> statuses, Pageable pageable);
}
