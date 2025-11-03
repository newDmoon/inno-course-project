package org.innowise.authservice.client;

import org.innowise.authservice.model.dto.RegistrationRequest;
import org.innowise.authservice.model.dto.UserDTO;
import org.innowise.authservice.util.ApplicationConstant;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service", url = "${services.user-service.url}")
public interface UserClient {
    @PostMapping("/api/v1/users")
    UserDTO createUser(@RequestBody RegistrationRequest registrationRequest);

    @DeleteMapping("/api/v1/users/{id}")
    void deleteUserById(@PathVariable(ApplicationConstant.ID) Long id);
}
