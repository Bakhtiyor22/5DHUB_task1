package org.example.companyservice.controller;

import org.example.companyservice.dto.*;
import org.example.companyservice.service.CompanyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/companies")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping
    public Page<CompanyResponseDto> getAllCompanies(Pageable pageable) {
        return companyService.getAllCompanies(pageable);
    }

    @GetMapping("/{id}")
    public CompanyResponseDto getCompanyById(@PathVariable Long id) {
        return companyService.getCompanyById(id);
    }

    @GetMapping("/simple/{id}")
    public CompanyDto getSimpleCompanyById(@PathVariable Long id) {
        return companyService.getSimpleCompanyById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompanyResponseDto createCompany(@Valid @RequestBody CompanyCreateRequestDto companyCreateRequestDto) {
        return companyService.createCompany(companyCreateRequestDto);
    }

    @PutMapping("/{id}")
    public CompanyResponseDto updateCompany(@PathVariable Long id, @Valid @RequestBody CompanyUpdateRequestDto companyUpdateRequestDto) {
        return companyService.updateCompany(id, companyUpdateRequestDto);
    }

    @PostMapping("/{companyId}/employees")
    public CompanyResponseDto assignEmployeeToCompany(
            @PathVariable Long companyId,
            @Valid @RequestBody EmployeeAssignmentRequestDto request) {
        return companyService.assignEmployee(companyId, request.userId());
    }

    @DeleteMapping("/{companyId}/employees/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeEmployeeFromCompany(
            @PathVariable Long companyId,
            @PathVariable String userId) {
        companyService.removeEmployee(companyId, userId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
    }
}