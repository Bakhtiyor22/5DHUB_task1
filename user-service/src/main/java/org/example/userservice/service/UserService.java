package org.example.userservice.service;

import org.example.userservice.company.CompanyClient;
import org.example.userservice.dto.*;
import org.example.userservice.entity.User;
import org.example.userservice.exception.BadRequestException;
import org.example.userservice.exception.ResourceNotFoundException;
import org.example.userservice.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CompanyClient companyClient;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, CompanyClient companyClient, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.companyClient = companyClient;
        this.userMapper = userMapper;
    }

    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public UserResponseDto getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return mapToResponseDto(user);
    }

    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        if (userRequestDto.firstName() == null || userRequestDto.firstName().isEmpty()) {
            throw new BadRequestException("Username is required");
        }
        User user = new User();
        user.setFirstName(userRequestDto.firstName());
        user.setLastName(userRequestDto.lastName());
        user.setPhoneNumber(userRequestDto.phoneNumber());

        User savedUser = userRepository.save(user);
        return userMapper.fromUserResponseDto(savedUser, null);
    }

    public UserResponseDto updateUser(String id, UserRequestDto userRequestDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (userRequestDto.firstName() != null && !userRequestDto.firstName().trim().isEmpty()) {
            existingUser.setFirstName(userRequestDto.firstName());
        }
        if (userRequestDto.lastName() != null && !userRequestDto.lastName().trim().isEmpty()) {
            existingUser.setLastName(userRequestDto.lastName());
        }
        if (userRequestDto.phoneNumber() != null) {
            existingUser.setPhoneNumber(userRequestDto.phoneNumber());
        }

        User updatedUser = userRepository.save(existingUser);
        CompanyDto companyDto = null;
        if (updatedUser.getCompanyId() != null) {
            try {
                companyDto = companyClient.getCompanyById(updatedUser.getCompanyId());
            } catch (Exception e) {
                System.err.println("Failed to fetch company details for companyId " + updatedUser.getCompanyId() + " for user " + updatedUser.getId() + ". Error: " + e.getMessage());
            }
        }
        return userMapper.fromUserResponseDto(updatedUser, companyDto);
    }

    public UserResponseDto assignCompanyToUser(String userId, Long companyId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setCompanyId(companyId);
        User updatedUser = userRepository.save(user);
        return mapToResponseDto(updatedUser);
    }

    public UserResponseDto removeCompanyFromUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        user.setCompanyId(null);
        User updatedUser = userRepository.save(user);
        return mapToResponseDto(updatedUser);
    }

    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    private UserResponseDto mapToResponseDto(User user) {
        CompanyDto companyDto = null;
        if (user.getCompanyId() != null) {
            try {
                companyDto = companyClient.getCompanyById(user.getCompanyId());
            } catch (Exception e) {
                    System.err.println("Skipping company details for user " + user.getId() +
                        " due to service unavailability: " + e.getMessage());
            }
        }
        return userMapper.fromUserResponseDto(user, companyDto);
    }
}