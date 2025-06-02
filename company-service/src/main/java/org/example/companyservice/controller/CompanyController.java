package org.example.companyservice.controller;

import org.example.companyservice.dto.CompanyDto;
import org.example.companyservice.dto.CompanyRequestDto;
import org.example.companyservice.dto.CompanyResponseDto;
import org.example.companyservice.dto.EmployeeAssignmentRequestDto;
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
    public CompanyResponseDto createCompany(@Valid @RequestBody CompanyRequestDto companyRequestDto) {
        return companyService.createCompany(companyRequestDto);
    }

    @PutMapping("/{id}")
    public CompanyResponseDto updateCompany(@PathVariable Long id, @Valid @RequestBody CompanyRequestDto companyRequestDto) {
        return companyService.updateCompany(id, companyRequestDto);
    }

    @PostMapping("/{companyId}/employees")
    public CompanyResponseDto assignEmployeeToCompany(
            @PathVariable Long companyId,
            @Valid @RequestBody EmployeeAssignmentRequestDto request) {
        return companyService.assignEmployee(companyId, request.userId());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
    }
}