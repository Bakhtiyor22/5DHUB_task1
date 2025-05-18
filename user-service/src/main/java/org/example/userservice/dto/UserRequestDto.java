package org.example.userservice.dto;

public record UserRequestDto(
    String id,
    String firstName,
    String lastName,
    String phoneNumber,
    CompanyDto companyDto
){}