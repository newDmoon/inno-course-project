package org.innowise.authservice.client;

import org.innowise.authservice.model.dto.UserCreateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service", url = "${services.user-service.url}")
public interface UserClient {
    @PostMapping("/api/v1/users")
    void createUser(@RequestBody UserCreateRequest request);
}
