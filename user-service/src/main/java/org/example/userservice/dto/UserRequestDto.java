package org.example.userservice.dto;

public record UserRequestDto(
    String firstName,
    String lastName,
    String phoneNumber
){}