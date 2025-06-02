package org.example.companyservice.user;

import org.example.companyservice.dto.CompanyAssignmentRequestDto;
import org.example.companyservice.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@FeignClient(
        name = "user-service"
)
public interface UserClient {
    @GetMapping("/api/v1/users/{id}")
    Optional<UserDto> getUserById(@PathVariable("id") String id);

    @PutMapping("/api/v1/users/{userId}/company")
    void assignCompanyToUser(@PathVariable("userId") String userId, @RequestBody CompanyAssignmentRequestDto request);

    @DeleteMapping("/api/v1/users/{userId}/company")
    void removeCompanyFromUser(@PathVariable("userId") String userId);
}