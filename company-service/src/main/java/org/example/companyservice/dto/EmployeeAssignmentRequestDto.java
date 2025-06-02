package org.example.companyservice.dto;

import jakarta.validation.constraints.NotBlank;

public record EmployeeAssignmentRequestDto(
        @NotBlank(message = "User ID is required")
        String userId
) {
}