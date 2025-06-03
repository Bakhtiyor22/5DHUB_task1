package org.example.userservice.service.impl;

import org.example.userservice.company.CompanyClient;
import org.example.userservice.dto.*;
import org.example.userservice.entity.User;
import org.example.userservice.exception.custom.DuplicateResourceException;
import org.example.userservice.exception.custom.InvalidInputException;
import org.example.userservice.exception.custom.ResourceNotFoundException;
import org.example.userservice.mapper.UserMapper;
import org.example.userservice.repository.UserRepository;
import org.example.userservice.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.logging.Logger;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = Logger.getLogger(UserServiceImpl.class.getName());

    private final UserRepository userRepository;
    private final CompanyClient companyClient;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, CompanyClient companyClient, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.companyClient = companyClient;
        this.userMapper = userMapper;
    }

    @Override
    public Page<UserResponseDto> getAllUsers(Pageable pageable) {
        logger.info("Fetching all users with pagination");
        return userRepository.findAll(pageable)
                .map(this::mapToResponseDto);
    }

    @Override
    public UserResponseDto getUserById(String id) {
        logger.info("Fetching user with id: " + id);
        User user = findUserByIdOrThrow(id);
        return mapToResponseDto(user);
    }


    @Override
    public UserResponseDto createUser(UserCreateRequestDto userCreateRequestDto) {
        logger.info("Creating new user");
        validateUserCreateRequest(userCreateRequestDto);

        User user = new User();
        user.setFirstName(userCreateRequestDto.firstName());
        user.setLastName(userCreateRequestDto.lastName());
        user.setPhoneNumber(userCreateRequestDto.phoneNumber());

        User savedUser = userRepository.save(user);
        logger.info("Created user with id: " + savedUser.getId());
        return userMapper.fromUserResponseDto(savedUser, null);
    }

    @Override
    public UserResponseDto updateUser(String id, UserUpdateRequestDto userUpdateRequestDto) {
        logger.info("Updating user with id: " + id);
        User existingUser = findUserByIdOrThrow(id);
        validateUserUpdateRequest(id, userUpdateRequestDto);

        updateUserFields(existingUser, userUpdateRequestDto);
        User updatedUser = userRepository.save(existingUser);

        logger.info("Updated user with id: " + id);
        return mapToResponseDto(updatedUser);
    }

    @Override
    public UserResponseDto assignCompanyToUser(String userId, Long companyId) {
        logger.info("Assigning company " + companyId + " to user " + userId);
        User user = findUserByIdOrThrow(userId);
        user.setCompanyId(companyId);
        User updatedUser = userRepository.save(user);
        return mapToResponseDto(updatedUser);
    }

    @Override
    public UserResponseDto removeCompanyFromUser(String userId) {
        logger.info("Removing company from user " + userId);
        User user = findUserByIdOrThrow(userId);
        user.setCompanyId(null);
        User updatedUser = userRepository.save(user);
        return mapToResponseDto(updatedUser);
    }

    @Override
    public void deleteUser(String id) {
        logger.info("Deleting user with id: " + id);
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    private User findUserByIdOrThrow(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private void validateUserCreateRequest(UserCreateRequestDto userCreateRequestDto) {
        if (userRepository.findByPhoneNumber(userCreateRequestDto.phoneNumber()).isPresent()) {
            throw new DuplicateResourceException("Phone number already exists");
        }
    }

    private void validateUserUpdateRequest(String userId, UserUpdateRequestDto userUpdateRequestDto) {
        if (userUpdateRequestDto.phoneNumber() != null) {
            Optional<User> existingUserWithPhone = userRepository.findByPhoneNumber(userUpdateRequestDto.phoneNumber());
            if (existingUserWithPhone.isPresent() && !existingUserWithPhone.get().getId().equals(userId)) {
                throw new DuplicateResourceException("Phone number already exists");
            }
        }
    }

    private void updateUserFields(User user, UserUpdateRequestDto userUpdateRequestDto) {
        if (userUpdateRequestDto.firstName() != null && !userUpdateRequestDto.firstName().trim().isEmpty()) {
            user.setFirstName(userUpdateRequestDto.firstName());
        }
        if (userUpdateRequestDto.lastName() != null && !userUpdateRequestDto.lastName().trim().isEmpty()) {
            user.setLastName(userUpdateRequestDto.lastName());
        }
        if (userUpdateRequestDto.phoneNumber() != null) {
            user.setPhoneNumber(userUpdateRequestDto.phoneNumber());
        }
    }

    private UserResponseDto mapToResponseDto(User user) {
        CompanyDto companyDto = null;
        if (user.getCompanyId() != null) {
            try {
                companyDto = companyClient.getCompanyById(user.getCompanyId());
            } catch (Exception e) {
                logger.warning("Failed to fetch company details for user " + user.getId() +
                        ": " + e.getMessage());
            }
        }
        return userMapper.fromUserResponseDto(user, companyDto);
    }
}