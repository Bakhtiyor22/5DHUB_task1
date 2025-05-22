package org.example.companyservice.user;

import org.example.companyservice.dto.CompanyAssignmentRequestDto; // Import the new DTO
import org.example.companyservice.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping; // Import DeleteMapping
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping; // Import PutMapping
import org.springframework.web.bind.annotation.RequestBody;  // Import RequestBody

import java.util.Optional;

@FeignClient(
        name = "user-service"
)
public interface UserClient {
    @GetMapping("/api/v1/users/{id}")
    Optional<UserDto> getUserById(@PathVariable("id") String id);

    // Method to tell user-service to assign a company to a user
    @PutMapping("/api/v1/users/{userId}/company")
    void assignCompanyToUser(@PathVariable("userId") String userId, @RequestBody CompanyAssignmentRequestDto request);

    // Method to tell user-service to remove company from a user
    @DeleteMapping("/api/v1/users/{userId}/company")
    void removeCompanyFromUser(@PathVariable("userId") String userId);
}