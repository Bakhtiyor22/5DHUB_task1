package org.example.userservice.dto;

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
