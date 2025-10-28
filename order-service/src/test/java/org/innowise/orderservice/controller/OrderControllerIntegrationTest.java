package org.innowise.orderservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.innowise.orderservice.exception.NotFoundException;
import org.innowise.orderservice.model.OrderStatus;
import org.innowise.orderservice.model.dto.OrderDTO;
import org.innowise.orderservice.model.dto.OrderFilterDTO;
import org.innowise.orderservice.service.OrderService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public OrderService orderService() {
            return Mockito.mock(OrderService.class);
        }
    }

    private OrderDTO createTestOrderDTO(Long id, Long userId, OrderStatus status) {
        return OrderDTO.builder()
                .id(id)
                .userId(userId)
                .status(status)
                .creationDate(LocalDateTime.now())
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createOrder_withAdminRole_shouldReturnCreatedOrder() throws Exception {
        OrderDTO request = OrderDTO.builder().id(1L).userId(10L).status(OrderStatus.PENDING).build();
        OrderDTO response = OrderDTO.builder().id(1L).userId(10L).status(OrderStatus.PENDING).build();

        Mockito.when(orderService.createOrder(any(OrderDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(10));
    }

    @Test
    void createOrder_withoutAuthentication_shouldReturnForbidden() throws Exception {
        OrderDTO request = createTestOrderDTO(null, 10L, OrderStatus.PENDING);

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createOrder_withInvalidData_shouldReturnBadRequest() throws Exception {
        OrderDTO invalidRequest = createTestOrderDTO(null, null, OrderStatus.PENDING);


        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getOrderById_shouldReturnOrder() throws Exception {
        OrderDTO order = OrderDTO.builder().id(100L).userId(5L).build();

        Mockito.when(orderService.getOrderById(100L)).thenReturn(order);

        mockMvc.perform(get("/api/v1/orders/{id}", 100L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.userId").value(5));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getOrderById_notFound_shouldReturnNotFound() throws Exception {
        Mockito.when(orderService.getOrderById(999L))
                .thenThrow(new NotFoundException(999L));

        mockMvc.perform(get("/api/v1/orders/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getOrders_withPagination_shouldReturnPage() throws Exception {
        List<OrderDTO> orders = List.of(
                createTestOrderDTO(1L, 10L, OrderStatus.PENDING),
                createTestOrderDTO(2L, 11L, OrderStatus.CONFIRMED)
        );

        Pageable pageable = PageRequest.of(0, 10);
        Page<OrderDTO> page = new PageImpl<>(orders, pageable, orders.size());

        Mockito.when(orderService.getOrders(any(OrderFilterDTO.class), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/orders")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[1].userId").value(11));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateOrder_notFound_shouldReturnNotFound() throws Exception {
        OrderDTO updated = createTestOrderDTO(999L, 42L, OrderStatus.CONFIRMED);

        Mockito.when(orderService.updateOrderById(eq(999L), any(OrderDTO.class)))
                .thenThrow(new NotFoundException(999L));

        mockMvc.perform(put("/api/v1/orders/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateOrder_shouldReturnUpdatedOrder() throws Exception {
        OrderDTO updated = OrderDTO.builder().id(200L).userId(42L).status(OrderStatus.CONFIRMED).build();

        Mockito.when(orderService.updateOrderById(eq(200L), any(OrderDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/v1/orders/{id}", 200L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(200))
                .andExpect(jsonPath("$.userId").value(42));
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateOrder_withUserRole_shouldReturnForbidden() throws Exception {
        OrderDTO updated = createTestOrderDTO(200L, 42L, OrderStatus.CONFIRMED);

        mockMvc.perform(put("/api/v1/orders/{id}", 200L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteOrder_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/orders/{id}", 5L))
                .andExpect(status().isNoContent());

        Mockito.verify(orderService).deleteOrderById(5L);
    }
}
