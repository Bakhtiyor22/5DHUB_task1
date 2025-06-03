package org.example.companyservice.service;

import org.example.companyservice.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CompanyService {
    Page<CompanyResponseDto> getAllCompanies(Pageable pageable);
    CompanyResponseDto getCompanyById(Long id);
    CompanyDto getSimpleCompanyById(Long id);
    CompanyResponseDto createCompany(CompanyCreateRequestDto companyCreateRequestDto);
    CompanyResponseDto updateCompany(Long id, CompanyUpdateRequestDto companyUpdateRequestDto);
    CompanyResponseDto assignEmployee(Long companyId, String userId);
    void removeEmployee(Long companyId, String userId);
    void deleteCompany(Long id);
}