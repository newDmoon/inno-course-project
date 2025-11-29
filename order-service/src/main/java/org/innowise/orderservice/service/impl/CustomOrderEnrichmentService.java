package org.innowise.orderservice.service.impl;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.innowise.orderservice.client.UserClient;
import org.innowise.orderservice.exception.FetchException;
import org.innowise.orderservice.exception.NotFoundException;
import org.innowise.orderservice.model.dto.OrderDTO;
import org.innowise.orderservice.model.dto.UserDTO;
import org.innowise.orderservice.service.OrderEnrichmentService;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOrderEnrichmentService implements OrderEnrichmentService {
    private final UserClient userClient;

    @Override
    @CircuitBreaker(name = "user-service")
    public OrderDTO enrichWithUser(OrderDTO orderDTO) {
        UserDTO userDTO;
        try {
            userDTO = userClient.getUserById(orderDTO.userId());
        } catch (feign.FeignException.NotFound e) {
            throw new NotFoundException(orderDTO.userId());
        } catch (FeignException.Forbidden e) {
            throw new AuthorizationDeniedException("Auth exception");
        } catch (Exception e) {
            throw new FetchException("Failed to fetch user info for id %d".formatted(orderDTO.userId()), e);
        }

        return enrichOrder(orderDTO, userDTO);
    }

    @Override
    @CircuitBreaker(name = "user-service")
    public List<OrderDTO> enrichWithUsers(List<OrderDTO> orders) {
        if (orders.isEmpty()) {
            return orders;
        }

        List<Long> userIds = orders.stream()
                .map(OrderDTO::userId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<Long, UserDTO> usersMap = new HashMap<>();
        try {
            List<UserDTO> users = userClient.getUsersByIds(userIds);
            usersMap = users.stream()
                    .collect(Collectors.toMap(UserDTO::id, u -> u));
        } catch (Exception e) {
            log.error("Failed to batch fetch user info for ids={}: {}", userIds, e.getMessage());
            return orders.stream()
                    .map(order -> enrichOrder(order, null))
                    .toList();
        }

        Map<Long, UserDTO> finalUsersMap = usersMap;
        return orders.stream()
                .map(order -> enrichOrder(order, finalUsersMap.get(order.userId())))
                .toList();
    }

    public OrderDTO enrichOrder(OrderDTO order, UserDTO userDTO) {
        return OrderDTO.builder()
                .id(order.id())
                .userId(order.userId())
                .status(order.status())
                .creationDate(order.creationDate())
                .userDTO(userDTO)
                .build();
    }
}