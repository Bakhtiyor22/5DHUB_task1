package org.example.companyservice.service;

import org.example.companyservice.dto.CompanyRequestDto;
import org.example.companyservice.dto.CompanyResponseDto;
import org.example.companyservice.dto.UserDto;
import org.example.companyservice.entity.Company;
import org.example.companyservice.entity.CompanyMapper;
import org.example.companyservice.repository.CompanyRepository;
import org.example.companyservice.user.UserClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;
    private final UserClient userClient;


    public CompanyService(CompanyRepository companyRepository, RestTemplate restTemplate, CompanyMapper companyMapper, UserClient userClient) {
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
        this.userClient = userClient;
    }

    @Transactional(readOnly = true)
    public List<CompanyResponseDto> getAllCompanies() {
        return companyRepository.findAll().stream()
                .map(this::mapEntityToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CompanyResponseDto getCompanyById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Company not found with id: " + id));
        return mapEntityToResponseDto(company);
    }

    @Transactional
    public CompanyResponseDto createCompany(CompanyRequestDto companyRequestDto) {
        if (companyRequestDto.getName() == null || companyRequestDto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Company name cannot be empty");
        }
        Company company = companyMapper.toCompany(companyRequestDto);
        if (company.getEmployeeIds() == null) {
            company.setEmployeeIds(Collections.emptyList());
        }

        Company savedCompany = companyRepository.save(company);
        return mapEntityToResponseDto(savedCompany);
    }

    @Transactional
    public CompanyResponseDto updateCompany(Long id, CompanyRequestDto companyRequestDto) {
        Company existingCompany = companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Company not found with id: " + id));

        if (companyRequestDto.getName() == null || companyRequestDto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Company name cannot be empty");
        }

        existingCompany.setName(companyRequestDto.getName());
        existingCompany.setBudget(companyRequestDto.getBudget());
        existingCompany.setEmployeeIds(companyRequestDto.getEmployeeIds() != null ? companyRequestDto.getEmployeeIds() : Collections.emptyList());

        Company updatedCompany = companyRepository.save(existingCompany);
        return mapEntityToResponseDto(updatedCompany);
    }

    @Transactional
    public void deleteCompany(Long id) {
        if (!companyRepository.existsById(id)) {
            throw new EntityNotFoundException("Company not found with id: " + id);
        }
        companyRepository.deleteById(id);
    }

    private CompanyResponseDto mapEntityToResponseDto(Company company) {
        List<UserDto> employees = company.getEmployeeIds().stream()
                .map(this::fetchUserById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return companyMapper.toCompanyResponseDto(company, employees);
    }

    private UserDto fetchUserById(Long userId) {
        try {
            return userClient.getUserById(userId);
        } catch (Exception e) {
            System.err.println("Error fetching user with id " + userId + ": " + e.getMessage());
            return null;
        }
    }

}