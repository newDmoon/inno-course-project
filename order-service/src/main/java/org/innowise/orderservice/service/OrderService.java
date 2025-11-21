package org.innowise.orderservice.service;

import org.innowise.orderservice.model.dto.OrderDTO;
import org.innowise.orderservice.model.dto.OrderFilterDTO;
import org.innowise.orderservice.model.dto.PaymentCreatedEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for handling order operations including creation, retrieval,
 * updating, and deletion of orders with pagination and filtering support.
 *
 * @author Dmitry Novogrodsky
 * @version 1.0
 */
public interface OrderService {
    /**
     * Creates a new order with the provided order data.
     *
     * @param orderDTO the order data transfer object containing order details
     * @return OrderDTO containing the created order information
     */
    OrderDTO createOrder(OrderDTO orderDTO);

    /**
     * Retrieves an order by its unique identifier.
     *
     * @param id the order identifier to retrieve
     * @return OrderDTO containing the order information
     */
    OrderDTO getOrderById(Long id);

    /**
     * Retrieves a paginated list of orders with optional filtering criteria.
     *
     * @param filter the filter criteria for orders
     * @param pageable the pagination and sorting parameters
     * @return Page of OrderDTO containing the filtered and paginated orders
     */
    Page<OrderDTO> getOrders(OrderFilterDTO filter, Pageable pageable);

    /**
     * Updates an existing order with the provided data.
     *
     * @param id the order identifier to update
     * @param orderDTO the order data transfer object containing updated order details
     * @return OrderDTO containing the updated order information
     */
    OrderDTO updateOrderById(Long id, OrderDTO orderDTO);

    /**
     * Deletes an order by its unique identifier.
     *
     * @param id the order identifier to delete
     * @return true if the order was successfully deleted, false otherwise
     */
    boolean deleteOrderById(Long id);

    void updateOrderStatusFromPayment(PaymentCreatedEvent paymentCreatedEvent);
}