package org.example.companyservice.service;

import org.example.companyservice.dto.CompanyDto;
import org.example.companyservice.dto.CompanyRequestDto;
import org.example.companyservice.dto.CompanyResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CompanyService {
    Page<CompanyResponseDto> getAllCompanies(Pageable pageable);
    CompanyResponseDto getCompanyById(Long id);
    CompanyDto getSimpleCompanyById(Long id);
    CompanyResponseDto createCompany(CompanyRequestDto companyRequestDto);
    CompanyResponseDto updateCompany(Long id, CompanyRequestDto companyRequestDto);
    CompanyResponseDto assignEmployee(Long companyId, String userId);
    void deleteCompany(Long id);
}