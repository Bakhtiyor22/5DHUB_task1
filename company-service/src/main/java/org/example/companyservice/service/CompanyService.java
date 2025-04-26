package org.example.companyservice.service;

import org.example.companyservice.dto.CompanyResponseDto;
import org.example.companyservice.dto.UserDto;
import org.example.companyservice.entity.Company;
import org.example.companyservice.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${USER_SERVICE_URL}") // Inject URL from environment variable
    private String userServiceUrl;

    @Transactional(readOnly = true)
    public List<CompanyResponseDto> getAllCompanies() {
        return companyRepository.findAll().stream()
                .map(this::mapCompanyToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CompanyResponseDto getCompanyById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Company not found with id: " + id));
        return mapCompanyToResponseDto(company);
    }

    @Transactional
    public CompanyResponseDto createCompany(Company company) {
        // Basic validation
        if (company.getName() == null || company.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Company name cannot be empty");
        }
        // Ensure employeeIds list is not null if provided, or initialize
        if (company.getEmployeeIds() == null) {
            company.setEmployeeIds(Collections.emptyList());
        }
        // Optional: Validate employee IDs exist by calling user-service? (Can be complex)

        Company savedCompany = companyRepository.save(company);
        return mapCompanyToResponseDto(savedCompany);
    }

    @Transactional
    public CompanyResponseDto updateCompany(Long id, Company companyDetails) {
        Company existingCompany = companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Company not found with id: " + id));

        if (companyDetails.getName() == null || companyDetails.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Company name cannot be empty");
        }

        existingCompany.setName(companyDetails.getName());
        existingCompany.setBudget(companyDetails.getBudget());
        // Replace employee IDs - ensure list is not null
        existingCompany.setEmployeeIds(companyDetails.getEmployeeIds() != null ? companyDetails.getEmployeeIds() : Collections.emptyList());

        Company updatedCompany = companyRepository.save(existingCompany);
        return mapCompanyToResponseDto(updatedCompany);
    }

    @Transactional
    public void deleteCompany(Long id) {
        if (!companyRepository.existsById(id)) {
            throw new EntityNotFoundException("Company not found with id: " + id);
        }
        // Consider implications: what happens to users associated with this company?
        // The current setup doesn't automatically handle this.
        // You might need logic here or in the user service to update/delete users.
        companyRepository.deleteById(id);
    }

    // --- Helper Methods ---

    private CompanyResponseDto mapCompanyToResponseDto(Company company) {
        List<UserDto> employees = company.getEmployeeIds().stream()
                .map(this::getUserById)
                .filter(Objects::nonNull) // Filter out nulls if user fetch fails
                .collect(Collectors.toList());

        return new CompanyResponseDto(
                company.getId(),
                company.getName(),
                company.getBudget(),
                employees
        );
    }

    private UserDto getUserById(Long userId) {
        String url = userServiceUrl + "/api/users/" + userId;
        try {
            // Make the REST call - expecting UserResponseDto from user-service
            // We map it to UserDto for company-service context
            // Note: UserResponseDto in user-service contains CompanyDto, which we don't need here.
            // We need a DTO from user-service that *doesn't* require fetching company details again.
            // Let's assume user-service GET /api/users/{id} returns UserResponseDto for now.
            // A better approach would be a dedicated endpoint or DTO in user-service for this internal call.

            // For now, we'll use a generic Object and manually map, or adjust UserDto if needed.
            // Let's assume user-service returns something compatible with UserDto fields.
            UserDto user = restTemplate.getForObject(url, UserDto.class); // Assumes compatible structure
            if (user == null) {
                System.err.println("Warning: User not found for ID: " + userId + " at URL: " + url);
                return null; // Or return a placeholder UserDto
            }
            return user;
        } catch (RestClientException e) {
            System.err.println("Error fetching user details for ID: " + userId + " from URL: " + url + ". Error: " + e.getMessage());
            // Return null or a placeholder DTO to indicate the error
            return null; // Or new UserDto(userId, "Error Fetching User", null, null);
        } catch (Exception e) {
            System.err.println("Unexpected error fetching user details for ID: " + userId + ". Error: " + e.getMessage());
            return null;
        }
    }
}