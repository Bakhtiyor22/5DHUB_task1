package org.example.userservice.controller;

import org.example.userservice.dto.CompanyAssignmentRequestDto;
import org.example.userservice.dto.UserRequestDto;
import org.example.userservice.dto.UserResponseDto;
import org.example.userservice.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Page<UserResponseDto> getAllUsers(Pageable pageable) {
        return userService.getAllUsers(pageable);
    }

    @GetMapping("/{id}")
    public UserResponseDto getUserById(@PathVariable String id) {
        return userService.getUserById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto createUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        return userService.createUser(userRequestDto);
    }

    @PutMapping("/{id}")
    public UserResponseDto updateUser(@PathVariable String id, @Valid @RequestBody UserRequestDto userRequestDto) {
        return userService.updateUser(id, userRequestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
    }

    @PutMapping("/{userId}/company")
    public UserResponseDto assignCompanyToUser(
            @PathVariable String userId,
            @Valid @RequestBody CompanyAssignmentRequestDto request) {
        return userService.assignCompanyToUser(userId, request.companyId());
    }

    @DeleteMapping("/{userId}/company")
    public UserResponseDto removeCompanyFromUser(@PathVariable String userId) {
        return userService.removeCompanyFromUser(userId);
    }
}