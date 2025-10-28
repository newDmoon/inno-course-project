package org.innowise.orderservice.service.impl;

import org.innowise.orderservice.WireMockBaseTest;
import org.innowise.orderservice.model.dto.OrderDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Testcontainers
class CustomOrderEnrichmentServiceIntegrationTest extends WireMockBaseTest {
    @Autowired
    private CustomOrderEnrichmentService enrichmentService;

    @Test
    void enrichWithUser_whenValid_shouldReturnUserInfo() {
        stubFor(get(urlEqualTo("/api/v1/users/1"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":1,\"name\":\"Dmitry\"}")));

        OrderDTO order = OrderDTO.builder().id(100L).userId(1L).build();
        OrderDTO enrichedOrder = enrichmentService.enrichWithUser(order);

        assertThat(enrichedOrder.userDTO()).isNotNull();
        assertThat(enrichedOrder.userDTO().name()).isEqualTo("Dmitry");
    }

    @Test
    void enrichWithUser_whenUserServiceFails_shouldReturnOrderWithoutUser() {
        stubFor(get(urlEqualTo("/api/v1/users/2"))
                .willReturn(aResponse().withStatus(500)));

        OrderDTO order = OrderDTO.builder().id(101L).userId(2L).build();
        OrderDTO enrichedOrder = enrichmentService.enrichWithUser(order);

        assertThat(enrichedOrder.userDTO()).isNull();
    }

    @Test
    void enrichWithUsers_whenValid_shouldEnrichAllOrders() {
        stubFor(get(urlEqualTo("/api/v1/users/users?ids=1&ids=2"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"id\":1,\"name\":\"Dmitry\"},{\"id\":2,\"name\":\"Elizabeth\"}]")));

        List<OrderDTO> orders = List.of(
                OrderDTO.builder().id(100L).userId(1L).build(),
                OrderDTO.builder().id(101L).userId(2L).build()
        );

        List<OrderDTO> enrichedOrders = enrichmentService.enrichWithUsers(orders);

        assertThat(enrichedOrders).hasSize(2);
        assertThat(enrichedOrders.get(0).userDTO().name()).isEqualTo("Dmitry");
        assertThat(enrichedOrders.get(1).userDTO().name()).isEqualTo("Elizabeth");
    }
}