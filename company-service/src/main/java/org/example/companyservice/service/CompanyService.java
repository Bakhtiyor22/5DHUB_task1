package org.example.companyservice.service;

import org.example.companyservice.dto.CompanyAssignmentRequestDto; // For calling user-service
import org.example.companyservice.dto.CompanyRequestDto;
import org.example.companyservice.dto.CompanyResponseDto;
import org.example.companyservice.dto.UserDto;
import org.example.companyservice.entity.Company;
import org.example.companyservice.entity.CompanyMapper;
import org.example.companyservice.exception.BadRequestException;
import org.example.companyservice.exception.OperationFailureException;
import org.example.companyservice.exception.ResourceNotFoundException;
import org.example.companyservice.repository.CompanyRepository;
import org.example.companyservice.user.UserClient;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;
    private final UserClient userClient;

    public CompanyService(CompanyRepository companyRepository, CompanyMapper companyMapper, UserClient userClient) {
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
        this.userClient = userClient;
    }

    public List<CompanyResponseDto> getAllCompanies() {
        return companyRepository.findAll().stream()
                .map(this::mapEntityToResponseDto)
                .collect(Collectors.toList());
    }

    public CompanyResponseDto getCompanyById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));
        return mapEntityToResponseDto(company);
    }

    public CompanyResponseDto createCompany(CompanyRequestDto companyRequestDto) {
        if (companyRequestDto.getName() == null || companyRequestDto.getName().trim().isEmpty()) {
            throw new BadRequestException("Company name cannot be empty");
        }
        Company company = companyMapper.toCompany(companyRequestDto);
        Company savedCompany = companyRepository.save(company);
        return companyMapper.toCompanyResponseDto(savedCompany, Collections.emptyList());
    }

    public CompanyResponseDto updateCompany(Long id, CompanyRequestDto companyRequestDto) {
        Company existingCompany = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));

        if (companyRequestDto.getName() == null || companyRequestDto.getName().trim().isEmpty()) {
            throw new BadRequestException("Company name cannot be empty");
        }

        existingCompany.setName(companyRequestDto.getName());
        existingCompany.setBudget(companyRequestDto.getBudget());

        Company updatedCompany = companyRepository.save(existingCompany);
        return mapEntityToResponseDto(updatedCompany);
    }

    public CompanyResponseDto assignEmployee(Long companyId, String userId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + companyId));

        userClient.getUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId + ", cannot assign to company."));

        if (company.getEmployeeIds().contains(userId)) {
            throw new BadRequestException("User " + userId + " is already an employee of company " + companyId);
        }

        company.getEmployeeIds().add(userId);
        companyRepository.save(company);
        try {
            userClient.assignCompanyToUser(userId, new CompanyAssignmentRequestDto(companyId));
        } catch (Exception e) {
            throw new OperationFailureException("Failed to assign company to user in user-service for userId " + userId + ". Error: " + e.getMessage(), e);
        }
        return mapEntityToResponseDto(company);
    }

    public void deleteCompany(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));
        companyRepository.delete(company);
    }

    private CompanyResponseDto mapEntityToResponseDto(Company company) {
        List<Optional<UserDto>> employees = Collections.emptyList();
        if (company.getEmployeeIds() != null && !company.getEmployeeIds().isEmpty()) {
            employees = company.getEmployeeIds().stream()
                    .map(this::fetchUserByIdSafe)
                    .collect(Collectors.toList());
        }
        return companyMapper.toCompanyResponseDto(company, employees);
    }

    private Optional<UserDto> fetchUserByIdSafe(String userId) {
        try {
            return userClient.getUserById(userId);
        } catch (Exception e) {
            System.err.println("Error fetching user details for employee id " + userId + ": " + e.getMessage());
            return Optional.empty();
        }
    }
}