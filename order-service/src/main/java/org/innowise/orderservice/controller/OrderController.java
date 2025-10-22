package org.innowise.orderservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.innowise.orderservice.exception.NotFoundException;
import org.innowise.orderservice.model.dto.OrderDTO;
import org.innowise.orderservice.model.dto.OrderFilterDTO;
import org.springframework.data.domain.Page;
import org.innowise.orderservice.service.OrderService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for handling order operations.
 * Provides endpoints for order management including creation, retrieval, updating, and deletion.
 *
 * @author Dmitry Novogrodsky
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    /**
     * Creates a new order in the system.
     *
     * @param orderDTO the order request containing order details
     * @return ResponseEntity containing created OrderDTO with HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody OrderDTO orderDTO) {
        OrderDTO createdOrder = orderService.createOrder(orderDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    /**
     * Retrieves an order by its unique identifier.
     *
     * @param id the order identifier to retrieve
     * @return ResponseEntity containing OrderDTO with HTTP 200 status
     * @throws NotFoundException if order with specified ID is not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        OrderDTO order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    /**
     * Retrieves a paginated list of orders with optional filtering.
     * Supports pagination, sorting, and filtering by various criteria.
     *
     * @param filter the filter criteria for orders
     * @param pageable the pagination and sorting parameters
     * @return ResponseEntity containing Page of OrderDTO with HTTP 200 status
     */
    @GetMapping
    public ResponseEntity<Page<OrderDTO>> getOrders(
            OrderFilterDTO filter,
            @PageableDefault Pageable pageable) {
        Page<OrderDTO> ordersPage = orderService.getOrders(filter, pageable);
        return ResponseEntity.ok(ordersPage);
    }

    /**
     * Updates an existing order with the provided data.
     *
     * @param id the order identifier to update
     * @param orderDTO the order request containing updated order details
     * @return ResponseEntity containing updated OrderDTO with HTTP 200 status
     * @throws NotFoundException if order with specified ID is not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<OrderDTO> updateOrderById(@PathVariable Long id, @Valid @RequestBody OrderDTO orderDTO) {
        OrderDTO updatedOrder = orderService.updateOrderById(id, orderDTO);
        return ResponseEntity.ok(updatedOrder);
    }

    /**
     * Deletes an order by its unique identifier.
     *
     * @param id the order identifier to delete
     * @return ResponseEntity with no content and HTTP 204 status
     * @throws NotFoundException if order with specified ID is not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderById(@PathVariable Long id) {
        orderService.deleteOrderById(id);
        return ResponseEntity.noContent().build();
    }
}