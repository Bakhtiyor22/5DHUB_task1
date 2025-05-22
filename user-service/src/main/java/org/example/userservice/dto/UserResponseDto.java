package org.example.userservice.dto;

import java.util.Optional;

public record UserResponseDto(
    String id,
    String firstName,
    String lastName,
    String phoneNumber,
    Optional<CompanyDto> companyDto
) {}

