package org.innowise.orderservice.client;

import org.innowise.orderservice.model.dto.UserDTO;
import org.innowise.orderservice.util.ApplicationConstant;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${user.service.url}")
public interface UserClient {

    @GetMapping("/api/v1/users/{id}")
    UserDTO getUserById(@PathVariable(ApplicationConstant.ID) Long id);
}