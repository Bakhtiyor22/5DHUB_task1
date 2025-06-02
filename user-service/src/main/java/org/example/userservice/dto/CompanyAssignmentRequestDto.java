package org.example.userservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CompanyAssignmentRequestDto(
        @NotNull(message = "Company ID is required")
        @Positive(message = "Company ID must be positive")
        Long companyId
) {}
