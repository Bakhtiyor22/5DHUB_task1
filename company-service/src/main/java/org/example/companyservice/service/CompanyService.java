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

    @Value("${USER_SERVICE_URL}")
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
        if (company.getName() == null || company.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Company name cannot be empty");
        }
        if (company.getEmployeeIds() == null) {
            company.setEmployeeIds(Collections.emptyList());
        }

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
        existingCompany.setEmployeeIds(companyDetails.getEmployeeIds() != null ? companyDetails.getEmployeeIds() : Collections.emptyList());

        Company updatedCompany = companyRepository.save(existingCompany);
        return mapCompanyToResponseDto(updatedCompany);
    }

    @Transactional
    public void deleteCompany(Long id) {
        if (!companyRepository.existsById(id)) {
            throw new EntityNotFoundException("Company not found with id: " + id);
        }
        companyRepository.deleteById(id);
    }

    private CompanyResponseDto mapCompanyToResponseDto(Company company) {
        List<UserDto> employees = company.getEmployeeIds().stream()
                .map(this::getUserById)
                .filter(Objects::nonNull) 
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
            UserDto user = restTemplate.getForObject(url, UserDto.class);
            if (user == null) {
                System.err.println("Warning: User not found for ID: " + userId + " at URL: " + url);
                return null; 
            }
            return user;
        } catch (RestClientException e) {
            System.err.println("Error fetching user details for ID: " + userId + " from URL: " + url + ". Error: " + e.getMessage());
            return null; 
        } catch (Exception e) {
            System.err.println("Unexpected error fetching user details for ID: " + userId + ". Error: " + e.getMessage());
            return null;
        }
    }
}