package org.example.companyservice.service.impl;

import org.example.companyservice.dto.*;
import org.example.companyservice.entity.Company;
import org.example.companyservice.exception.custom.DuplicateResourceException;
import org.example.companyservice.mapper.CompanyMapper;
import org.example.companyservice.exception.custom.BadRequestException;
import org.example.companyservice.exception.custom.ResourceNotFoundException;
import org.example.companyservice.repository.CompanyRepository;
import org.example.companyservice.service.CompanyService;
import org.example.companyservice.user.UserClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class CompanyServiceImpl implements CompanyService {

    private static final Logger logger = Logger.getLogger(CompanyServiceImpl.class.getName());

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;
    private final UserClient userClient;

    public CompanyServiceImpl(CompanyRepository companyRepository, CompanyMapper companyMapper, UserClient userClient) {
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
        this.userClient = userClient;
    }

    @Override
    public Page<CompanyResponseDto> getAllCompanies(Pageable pageable) {
        logger.info("Fetching all companies with pagination");
        return companyRepository.findAll(pageable)
                .map(this::mapEntityToResponseDto);
    }

    @Override
    public CompanyResponseDto getCompanyById(Long id) {
        logger.info("Fetching company with id: " + id);
        Company company = findCompanyByIdOrThrow(id);
        return mapEntityToResponseDto(company);
    }

    @Override
    public CompanyDto getSimpleCompanyById(Long id) {
        logger.info("Fetching simple company details for id: " + id);
        Company company = findCompanyByIdOrThrow(id);
        return new CompanyDto(company.getId(), company.getName(), company.getBudget());
    }

    @Override
    public CompanyResponseDto createCompany(CompanyRequestDto companyRequestDto) {
        logger.info("Creating new company");
        validateCompanyRequest(companyRequestDto, null);

        Company company = companyMapper.toCompany(companyRequestDto);
        Company savedCompany = companyRepository.save(company);

        logger.info("Created company with id: " + savedCompany.getId());
        return companyMapper.toCompanyResponseDto(savedCompany, Collections.emptyList());
    }

    @Override
    public CompanyResponseDto updateCompany(Long id, CompanyRequestDto companyRequestDto) {
        logger.info("Updating company with id: " + id);
        Company existingCompany = findCompanyByIdOrThrow(id);
        validateCompanyRequest(companyRequestDto, id);

        updateCompanyFields(existingCompany, companyRequestDto);
        Company updatedCompany = companyRepository.save(existingCompany);

        logger.info("Updated company with id: " + id);
        return mapEntityToResponseDto(updatedCompany);
    }

    @Override
    public CompanyResponseDto assignEmployee(Long companyId, String userId) {
        logger.info("Assigning user " + userId + " to company " + companyId);

        Company company = findCompanyByIdOrThrow(companyId);
        validateUserExists(userId);
        validateEmployeeNotAlreadyAssigned(company, userId);

        company.getEmployeeIds().add(userId);
        companyRepository.save(company);

        assignCompanyToUserInUserService(userId, companyId);

        logger.info("Successfully assigned user " + userId + " to company " + companyId);
        return mapEntityToResponseDto(company);
    }

    @Override
    public void deleteCompany(Long id) {
        logger.info("Deleting company with id: " + id);
        Company company = findCompanyByIdOrThrow(id);

        if (company.getEmployeeIds() != null && !company.getEmployeeIds().isEmpty()) {
            removeCompanyFromAllEmployees(company.getEmployeeIds());
        }

        companyRepository.delete(company);
        logger.info("Deleted company with id: " + id);
    }

    private Company findCompanyByIdOrThrow(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));
    }

    private void validateCompanyRequest(CompanyRequestDto companyRequestDto, Long companyId) {
        if (companyRequestDto.getName() == null || companyRequestDto.getName().trim().isEmpty()) {
            throw new BadRequestException("Company name cannot be empty");
        }
        if (companyRequestDto.getBudget() != null && companyRequestDto.getBudget() < 0) {
            throw new BadRequestException("Company budget cannot be negative");
        }

        Optional<Company> existingCompany = companyRepository.findByName(companyRequestDto.getName());

        if (existingCompany.isPresent()) {
            // For create (companyId is null) or if the found company is different than the one being updated
            if (companyId == null || !existingCompany.get().getId().equals(companyId)) {
                throw new DuplicateResourceException("Company name already exists");
            }
        }
    }

    private void validateUserExists(String userId) {
        userClient.getUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId + ", cannot assign to company."));
    }

    private void validateEmployeeNotAlreadyAssigned(Company company, String userId) {
        if (company.getEmployeeIds() != null && company.getEmployeeIds().contains(userId)) {
            throw new BadRequestException("User " + userId + " is already an employee of company " + company.getId());
        }
    }

    private void updateCompanyFields(Company company, CompanyRequestDto companyRequestDto) {
        company.setName(companyRequestDto.getName());
        company.setBudget(companyRequestDto.getBudget());

        if (companyRequestDto.getEmployeeIds() != null) {
            company.setEmployeeIds(companyRequestDto.getEmployeeIds());
        }
    }

    private void assignCompanyToUserInUserService(String userId, Long companyId) {
        try {
            userClient.assignCompanyToUser(userId, new CompanyAssignmentRequestDto(companyId));
        } catch (Exception e) {
            logger.severe("Failed to assign company to user in user-service for userId " + userId + ": " + e.getMessage());
            throw new BadRequestException("Failed to assign company to user in user-service for userId " + userId);
        }
    }

    private void removeCompanyFromAllEmployees(List<String> employeeIds) {
        for (String employeeId : employeeIds) {
            try {
                userClient.removeCompanyFromUser(employeeId);
            } catch (Exception e) {
                logger.warning("Failed to remove company assignment from user " + employeeId + ": " + e.getMessage());
            }
        }
    }

    private CompanyResponseDto mapEntityToResponseDto(Company company) {
        List<Optional<UserDto>> employees = fetchEmployeeDetails(company);
        return companyMapper.toCompanyResponseDto(company, employees);
    }

    private List<Optional<UserDto>> fetchEmployeeDetails(Company company) {
        if (company.getEmployeeIds() == null || company.getEmployeeIds().isEmpty()) {
            return Collections.emptyList();
        }

        return company.getEmployeeIds().stream()
                .map(this::fetchUserByIdSafe)
                .collect(Collectors.toList());
    }

    private Optional<UserDto> fetchUserByIdSafe(String userId) {
        try {
            return userClient.getUserById(userId);
        } catch (Exception e) {
            logger.warning("Error fetching user details for employee id " + userId + ": " + e.getMessage());
            return Optional.empty();
        }
    }
}