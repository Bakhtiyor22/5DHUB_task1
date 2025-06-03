package org.example.userservice.service;

import org.example.userservice.dto.UserCreateRequestDto;
import org.example.userservice.dto.UserUpdateRequestDto;
import org.example.userservice.dto.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Page<UserResponseDto> getAllUsers(Pageable pageable);
    UserResponseDto getUserById(String id);
    UserResponseDto createUser(UserCreateRequestDto userCreateRequestDto);
    UserResponseDto updateUser(String id, UserUpdateRequestDto userUpdateRequestDto);
    UserResponseDto assignCompanyToUser(String userId, Long companyId);
    UserResponseDto removeCompanyFromUser(String userId);
    void deleteUser(String id);
}