package org.example.companyservice.user;

import org.example.companyservice.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "user-service",
        url = "${application.config.users-url}"
)
public interface UserClient {
    @GetMapping("/{id}")
    UserDto getUserById(@PathVariable("id") String id);
}