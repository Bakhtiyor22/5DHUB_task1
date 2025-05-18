package org.example.userservice.dto;

import org.example.userservice.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toUser(UserRequestDto userRequestDto) {
        if (userRequestDto == null) {
            return null;
        }
        return User.builder()
                .id(userRequestDto.id())
                .firstName(userRequestDto.firstName())
                .lastName(userRequestDto.lastName())
                .phoneNumber(userRequestDto.phoneNumber())
                .companyId(userRequestDto.companyDto().getId())
                .build();
    }

    public UserResponseDto fromUserResponseDto(User user, CompanyDto companyDto) {
        if (user == null) {
            return null;
        }
        return new UserResponseDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                companyDto
        );
    }   
}
