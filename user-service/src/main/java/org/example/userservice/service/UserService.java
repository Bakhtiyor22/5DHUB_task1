package org.example.userservice.service;

import org.example.userservice.dto.CompanyDto;
import org.example.userservice.dto.UserResponseDto;
import org.example.userservice.entity.User;
import org.example.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${COMPANY_SERVICE_URL}") 
    private String companyServiceUrl;

    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapUserToResponseDto)
                .collect(Collectors.toList());
    }

    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        return mapUserToResponseDto(user);
    }

    public UserResponseDto createUser(User user) {
        if (user.getCompanyId() == null) {
            throw new IllegalArgumentException("Company ID must be provided");
        }

        User savedUser = userRepository.save(user);
        return mapUserToResponseDto(savedUser);
    }

    @Transactional
    public UserResponseDto updateUser(Long id, User userDetails) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        if (userDetails.getCompanyId() == null) {
            throw new IllegalArgumentException("Company ID must be provided");
        }

        existingUser.setFirstName(userDetails.getFirstName());
        existingUser.setLastName(userDetails.getLastName());
        existingUser.setPhoneNumber(userDetails.getPhoneNumber());
        existingUser.setCompanyId(userDetails.getCompanyId());

        User updatedUser = userRepository.save(existingUser);
        return mapUserToResponseDto(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    private UserResponseDto mapUserToResponseDto(User user) {
        CompanyDto companyDto = getCompanyById(user.getCompanyId());
        return new UserResponseDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                companyDto
        );
    }

    private CompanyDto getCompanyById(Long companyId) {
        String url = companyServiceUrl + "/api/companies/" + companyId;
        try {
            CompanyDto company = restTemplate.getForObject(url, CompanyDto.class);
            if (company == null) {
                System.err.println("Warning: Company not found for ID: " + companyId + " at URL: " + url);
                return new CompanyDto(companyId, "Company Not Found", null);
            }
            return company;
        } catch (Exception e) {
            System.err.println("Error fetching company details for ID: " + companyId + " from URL: " + url + ". Error: " + e.getMessage());
            return new CompanyDto(companyId, "Error Fetching Company", null);
        }
    }
}