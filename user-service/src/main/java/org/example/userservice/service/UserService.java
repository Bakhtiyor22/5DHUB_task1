package org.example.userservice.service;

import org.example.userservice.company.CompanyClient;
import org.example.userservice.dto.*;
import org.example.userservice.entity.User;
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

    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id.toString())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return mapToResponseDto(user);
    }

    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        if (userRequestDto.companyDto() == null || userRequestDto.companyDto().getId() == null) {
            throw new IllegalArgumentException("Company ID is required");
        }
        
        CompanyDto company = companyClient.getCompanyById(userRequestDto.companyDto().getId());
        
        User user = userMapper.toUser(userRequestDto);
        
        User savedUser = userRepository.save(user);
        return mapToResponseDto(savedUser);
    }

    public UserResponseDto updateUser(Long id, UserRequestDto userRequestDto) {
        User existingUser = userRepository.findById(id.toString())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        existingUser.setFirstName(userRequestDto.firstName());
        existingUser.setLastName(userRequestDto.lastName());
        existingUser.setPhoneNumber(userRequestDto.phoneNumber());
        
        if (userRequestDto.companyDto() != null &&
            !userRequestDto.companyDto().getId().equals(existingUser.getCompanyId())) {
            CompanyDto company = companyClient.getCompanyById(userRequestDto.companyDto().getId());
            existingUser.setCompanyId(userRequestDto.companyDto().getId());
        }
        
        User updatedUser = userRepository.save(existingUser);
        return mapToResponseDto(updatedUser);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id.toString())) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id.toString());
    }
    
    private UserResponseDto mapToResponseDto(User user) {
        CompanyDto companyDto = companyClient.getCompanyById(user.getCompanyId());
        return userMapper.fromUserResponseDto(user, companyDto);
    }
}