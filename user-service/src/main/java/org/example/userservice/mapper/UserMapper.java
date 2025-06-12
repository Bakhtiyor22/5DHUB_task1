package org.example.userservice.mapper;

import org.example.userservice.dto.CompanyDto;
import org.example.userservice.dto.UserCreateRequestDto;
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

    public User fromCreateRequestDto(UserCreateRequestDto requestDto) {
        if (requestDto == null) {
            return null;
        }
        User user = new User();
        user.setFirstName(requestDto.firstName());
        user.setLastName(requestDto.lastName());
        user.setPhoneNumber(requestDto.phoneNumber());
        return user;
    }
}