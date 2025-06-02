package org.example.userservice.mapper;

import org.example.userservice.dto.CompanyDto;
import org.example.userservice.dto.UserRequestDto;
import org.example.userservice.dto.UserResponseDto;
import org.example.userservice.entity.User;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserMapper {

    public UserResponseDto fromUserResponseDto(User user, CompanyDto companyDto) {
        if (user == null) {
            return null;
        }
        return new UserResponseDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                Optional.ofNullable(companyDto)
        );
    }
}