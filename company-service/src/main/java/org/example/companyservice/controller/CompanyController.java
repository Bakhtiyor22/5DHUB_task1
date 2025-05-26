package org.example.companyservice.controller;

import org.example.companyservice.dto.CompanyDto;
import org.example.companyservice.dto.CompanyRequestDto;
import org.example.companyservice.dto.CompanyResponseDto;
import org.example.companyservice.dto.EmployeeAssignmentRequestDto; // For request body
import org.example.companyservice.entity.Company;
import org.example.companyservice.exception.ResourceNotFoundException;
import org.example.companyservice.repository.CompanyRepository;
import org.example.companyservice.service.CompanyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/companies")
public class CompanyController {

    private final CompanyService companyService;
    private final CompanyRepository companyRepository;

    public CompanyController(CompanyService companyService, CompanyRepository companyRepository) {
        this.companyService = companyService;
        this.companyRepository = companyRepository;
    }

    @GetMapping
    public ResponseEntity<List<CompanyResponseDto>> getAllCompanies() {
        List<CompanyResponseDto> companies = companyService.getAllCompanies();
        return ResponseEntity.ok(companies);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponseDto> getCompanyById(@PathVariable Long id) {
        CompanyResponseDto company = companyService.getCompanyById(id);
        return ResponseEntity.ok(company);
    }

    @GetMapping("/simple/{id}")
    public ResponseEntity<CompanyDto> getSimpleCompanyById(@PathVariable Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));

        CompanyDto companyDto = new CompanyDto(company.getId(), company.getName(), company.getBudget());
        return ResponseEntity.ok(companyDto);
    }

    @PostMapping
    public ResponseEntity<CompanyResponseDto> createCompany(@RequestBody CompanyRequestDto companyRequestDto) {
        CompanyResponseDto createdCompany = companyService.createCompany(companyRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCompany);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompanyResponseDto> updateCompany(@PathVariable Long id, @RequestBody CompanyRequestDto companyRequestDto) {
        CompanyResponseDto updatedCompany = companyService.updateCompany(id, companyRequestDto);
        return ResponseEntity.ok(updatedCompany);
    }

    @PostMapping("/{companyId}/employees")
    public ResponseEntity<CompanyResponseDto> assignEmployeeToCompany(
            @PathVariable Long companyId,
            @RequestBody EmployeeAssignmentRequestDto request) {
        CompanyResponseDto updatedCompany = companyService.assignEmployee(companyId, request.userId());
        return ResponseEntity.ok(updatedCompany);
    }

     @DeleteMapping("/{id}")
     public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
         companyService.deleteCompany(id);
         return ResponseEntity.noContent().build();
     }
}