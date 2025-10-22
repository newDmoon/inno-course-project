package org.innowise.orderservice.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.innowise.orderservice.model.OrderStatus;

import java.time.LocalDateTime;

@Builder
public record OrderDTO(
        Long id,
        @NotNull(message = "User ID is required")
        Long userId,
        @NotNull(message = "Status is required")
        OrderStatus status,
        UserDTO userDTO,
        LocalDateTime creationDate) {
}
